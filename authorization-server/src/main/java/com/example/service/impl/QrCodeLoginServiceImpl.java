package com.example.service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.entity.Oauth2BasicUser;
import com.example.model.qrcode.QrCodeInfo;
import com.example.model.request.qrcode.QrCodeLoginConsentRequest;
import com.example.model.request.qrcode.QrCodeLoginScanRequest;
import com.example.model.response.qrcode.QrCodeGenerateResponse;
import com.example.model.response.qrcode.QrCodeLoginFetchResponse;
import com.example.model.response.qrcode.QrCodeLoginScanResponse;
import com.example.property.CustomSecurityProperties;
import com.example.service.IQrCodeLoginService;
import com.example.support.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * 二维码登录接口实现
 *
 * @author vains
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeLoginServiceImpl implements IQrCodeLoginService {

	private final RedisOperator<QrCodeInfo> redisOperator;

	private final RedisOperator<String> stringRedisOperator;

	private final CustomSecurityProperties customSecurityProperties;

	private final RedisOAuth2AuthorizationService authorizationService;

	private final RedisOperator<UsernamePasswordAuthenticationToken> authenticationRedisOperator;

	/**
     * 过期时间
     */
    private final long QR_CODE_INFO_TIMEOUT = 60 * 10;

	/**
     * 二维码信息前缀
     */
    private final String QR_CODE_PREV = "login:qrcode:";

	private final RequestCache requestCache = new HttpSessionRequestCache();

	@Override
    public QrCodeGenerateResponse generateQrCode() {
		// 生成二维码唯一id
		String qrCodeId = IdWorker.getIdStr();
		// 生成二维码并转为base64
		String pngQrCode = QrCodeUtil.generateAsBase64(qrCodeId, new QrConfig(), "png");
		QrCodeInfo info = QrCodeInfo.builder()
                .qrCodeId(qrCodeId)
                // 待扫描状态
                .qrCodeStatus(0)
                // 1分钟后过期
                .expiresTime(LocalDateTime.now().plusMinutes(2L))
                .build();

		// 获取当前request
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			// 获取当前session
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			DefaultSavedRequest savedRequest = (DefaultSavedRequest) this.requestCache.getRequest(request, response);
			if (savedRequest != null) {
				if (!UrlUtils.isAbsoluteUrl(customSecurityProperties.getLoginUrl())) {
					// 获取查询参数与请求路径
					String queryString = savedRequest.getQueryString();
					String requestUri = savedRequest.getRequestURI();
					// 前后端不分离根据请求路径和请求参数跳转
					info.setBeforeLoginRequestUri(requestUri);
					info.setBeforeLoginQueryString(queryString);
				}

				// 获取跳转登录之前访问url的query parameter
				String[] scopes = savedRequest.getParameterValues("scope");
				if (!ObjectUtils.isEmpty(scopes)) {
					// 不为空获取第一个并设置进二维码信息中
					info.setScopes(Set.of(scopes[0].split(" ")));
				}
				// 前端可以根据scope显示要获取的信息，或固定显示要获取的信息
			}
		}

		// 因为上边设置的过期时间是2分钟，这里设置10分钟过期，可根据业务自行调整过期时间
		redisOperator.set(QR_CODE_PREV + qrCodeId, info, QR_CODE_INFO_TIMEOUT);
		return new QrCodeGenerateResponse(qrCodeId, pngQrCode);
	}

	@Override
    public QrCodeLoginScanResponse scan(QrCodeLoginScanRequest loginScan) {
		// 应该用validation的
		Assert.hasLength(loginScan.getQrCodeId(), "二维码Id不能为空.");

		// 校验二维码状态
		QrCodeInfo info = redisOperator.get(QR_CODE_PREV + loginScan.getQrCodeId());
		if (info == null) {

			throw new RuntimeException("无效二维码.");
		}

		// 验证状态
		if (!Objects.equals(info.getQrCodeStatus(), 0)) {

			throw new RuntimeException("二维码已被其他人扫描，无法重复扫描.");
		}

		// 二维码是否过期
		boolean qrCodeExpire = info.getExpiresTime().isBefore(LocalDateTime.now());
		if (qrCodeExpire) {
			throw new RuntimeException("二维码已过期.");
		}

		QrCodeLoginScanResponse loginScanResponse = new QrCodeLoginScanResponse();

		// 获取登录用户信息
		OAuth2Authorization oAuth2Authorization = this.getOAuth2Authorization();
		if (oAuth2Authorization == null) {
			throw new OAuth2AuthenticationException(
					new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "登录已过期.", null));
		}
		// app端使用密码模式、手机认证登录，不使用三方登录的情况
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                oAuth2Authorization.getAttribute(Principal.class.getName());
		if (usernamePasswordAuthenticationToken.getPrincipal() instanceof Oauth2BasicUser basicUser) {
			// 生成临时票据
			String qrCodeTicket = IdWorker.getIdStr();
			// 根据二维码id和临时票据存储，确认时根据临时票据认证
			String redisQrCodeTicketKey = String.format("%s%s:%s", QR_CODE_PREV, loginScan.getQrCodeId(), qrCodeTicket);
			stringRedisOperator.set(redisQrCodeTicketKey, qrCodeTicket, QR_CODE_INFO_TIMEOUT);

			// 更新二维码信息的状态
			info.setQrCodeStatus(1);
			info.setName(basicUser.getName());
			info.setAvatarUrl(basicUser.getAvatarUrl());
			redisOperator.set(QR_CODE_PREV + loginScan.getQrCodeId(), info, QR_CODE_INFO_TIMEOUT);

			// 封装响应
			loginScanResponse.setQrCodeTicket(qrCodeTicket);
			loginScanResponse.setQrCodeStatus(0);
			loginScanResponse.setExpired(Boolean.FALSE);
			loginScanResponse.setScopes(info.getScopes());
		}

		// 其它登录方式暂不处理
		return loginScanResponse;
	}

	@Override
    public void consent(QrCodeLoginConsentRequest loginConsent) {
		// 应该用validation的
		Assert.hasLength(loginConsent.getQrCodeId(), "二维码Id不能为空.");

		// 校验二维码状态
		QrCodeInfo info = redisOperator.get(QR_CODE_PREV + loginConsent.getQrCodeId());
		if (info == null) {
			throw new RuntimeException("无效二维码或二维码已过期.");
		}

		// 验证临时票据
		String qrCodeTicketKey =
                String.format("%s%s:%s", QR_CODE_PREV, loginConsent.getQrCodeId(), loginConsent.getQrCodeTicket());
		String redisQrCodeTicket = stringRedisOperator.get(qrCodeTicketKey);
		if (!Objects.equals(redisQrCodeTicket, loginConsent.getQrCodeTicket())) {
			// 临时票据有误、临时票据失效(超过redis存活时间后确认)、redis数据有误
			if (log.isDebugEnabled()) {
				log.debug("临时票据有误、临时票据失效(超过redis存活时间后确认)、redis数据有误.");
			}
			throw new RuntimeException("登录确认失败，请重新扫描.");
		}
		// 使用后删除
		stringRedisOperator.delete(qrCodeTicketKey);

		// 获取登录用户信息
		OAuth2Authorization authorization = this.getOAuth2Authorization();
		if (authorization == null) {
			throw new OAuth2AuthenticationException(
					new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "登录已过期.", null));
		}

		// app端使用密码模式、手机认证登录，不使用三方登录的情况
		UsernamePasswordAuthenticationToken authenticationToken = authorization.getAttribute(Principal.class.getName());

		// 根据二维码id存储用户信息
		String redisUserinfoKey = String.format("%s%s:%s", QR_CODE_PREV, "userinfo", loginConsent.getQrCodeId());
		// 存储用户信息
		authenticationRedisOperator.set(redisUserinfoKey, authenticationToken, QR_CODE_INFO_TIMEOUT);

		// 更新二维码信息的状态
		info.setQrCodeStatus(2);
		redisOperator.set(QR_CODE_PREV + loginConsent.getQrCodeId(), info, QR_CODE_INFO_TIMEOUT);
	}

	@Override
    public QrCodeLoginFetchResponse fetch(String qrCodeId) {
		// 校验二维码状态
		QrCodeInfo info = redisOperator.get(QR_CODE_PREV + qrCodeId);
		if (info == null) {
			throw new RuntimeException("无效二维码或二维码已过期.");
		}

		QrCodeLoginFetchResponse loginFetchResponse = new QrCodeLoginFetchResponse();
		// 设置二维码是否过期、状态
		loginFetchResponse.setQrCodeStatus(info.getQrCodeStatus());
		loginFetchResponse.setExpired(info.getExpiresTime().isBefore(LocalDateTime.now()));

		if (!Objects.equals(info.getQrCodeStatus(), 0)) {
			// 如果是已扫描/已确认
			loginFetchResponse.setName(info.getName());
			loginFetchResponse.setAvatarUrl(info.getAvatarUrl());
		}

		// 如果是已确认，将之前扫码确认的用户信息放入当前session中
		if (Objects.equals(info.getQrCodeStatus(), 2)) {

			// 根据二维码id从redis获取用户信息
			String redisUserinfoKey = String.format("%s%s:%s", QR_CODE_PREV, "userinfo", qrCodeId);
			UsernamePasswordAuthenticationToken authenticationToken = authenticationRedisOperator.get(redisUserinfoKey);
			if (authenticationToken != null) {
				// 获取当前request
				RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
				if (requestAttributes == null) {
					throw new RuntimeException("获取当前Request失败.");
				}
				// 获取当前session
				HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
				HttpSession session = request.getSession(Boolean.FALSE);
				if (session != null) {
					// 获取到认证信息后将之前扫码确认的用户信息放入当前session中。
					session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(authenticationToken));

					// 操作成功后移除缓存
					redisOperator.delete(QR_CODE_PREV + qrCodeId);
					// 删除用户信息，防止其它人重放请求
					authenticationRedisOperator.delete(redisUserinfoKey);

					// 填充二维码数据，设置跳转到登录之前的请求路径、查询参数和是否授权申请请求
					loginFetchResponse.setBeforeLoginRequestUri(info.getBeforeLoginRequestUri());
					loginFetchResponse.setBeforeLoginQueryString(info.getBeforeLoginQueryString());
				}
			} else {
				throw new RuntimeException("获取登录确认用户信息失败.");
			}
		}

		return loginFetchResponse;
	}

	/**
     * 获取当前使用token对应的认证信息
     *
     * @return oauth2认证信息
     */
    private OAuth2Authorization getOAuth2Authorization() {
		// 校验登录状态
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new InsufficientAuthenticationException("未登录.");
		}
		if (authentication instanceof JwtAuthenticationToken jwtToken) {
			// jwt处理
			String tokenValue = jwtToken.getToken().getTokenValue();
			// 根据token获取授权登录时的认证信息(登录用户)
			return authorizationService.findByToken(tokenValue, OAuth2TokenType.ACCESS_TOKEN);
		}
		return null;
	}

}