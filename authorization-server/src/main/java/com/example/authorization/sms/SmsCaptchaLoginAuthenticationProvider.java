package com.example.authorization.sms;

import com.example.authorization.captcha.CaptchaAuthenticationProvider;
import com.example.constant.SecurityConstants;
import com.example.exception.InvalidCaptchaException;
import com.example.support.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static com.example.constant.RedisConstants.SMS_CAPTCHA_PREFIX_KEY;

/**
 * 短信验证码校验实现
 *
 * @author vains
 */
@Slf4j
@Component
public class SmsCaptchaLoginAuthenticationProvider extends CaptchaAuthenticationProvider {

    private final RedisOperator<String> redisOperator;

    /**
     * 利用构造方法在通过{@link Component}注解初始化时
     * 注入UserDetailsService和passwordEncoder，然后
     * 设置调用父类关于这两个属性的set方法设置进去
     *
     * @param userDetailsService 用户服务，给框架提供用户信息
     * @param passwordEncoder    密码解析器，用于加密和校验密码
     */
    public SmsCaptchaLoginAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, RedisOperator<String> redisOperator) {
        super(userDetailsService, passwordEncoder, redisOperator);
        this.redisOperator = redisOperator;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        log.info("Authenticate sms captcha...");

        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("The sms captcha cannot be empty.");
        }

        // 获取当前request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new InvalidCaptchaException("Failed to get the current request.");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录方式
        String loginType = request.getParameter(SecurityConstants.LOGIN_TYPE_NAME);
        // 获取grant_type
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        // 短信登录和自定义短信认证grant type会走下方认证
        // 如果是自定义密码模式则下方的认证判断只要判断下loginType即可
        // if (Objects.equals(loginType, SecurityConstants.SMS_LOGIN_TYPE)) {}
        if (Objects.equals(loginType, SecurityConstants.SMS_LOGIN_TYPE)
                || Objects.equals(grantType, SecurityConstants.GRANT_TYPE_SMS_CODE)) {
            // 获取存入缓存中的验证码(UsernamePasswordAuthenticationToken的principal中现在存入的是手机号)
            String smsCaptcha = redisOperator.get((SMS_CAPTCHA_PREFIX_KEY + authentication.getPrincipal()));
            // 校验输入的验证码是否正确(UsernamePasswordAuthenticationToken的credentials中现在存入的是输入的验证码)
            if (!Objects.equals(smsCaptcha, authentication.getCredentials())) {
                throw new BadCredentialsException("The sms captcha is incorrect.");
            }
            // 删除缓存
            redisOperator.delete((SMS_CAPTCHA_PREFIX_KEY + authentication.getPrincipal()));
            // 在这里也可以拓展其它登录方式，比如邮箱登录什么的
        } else {
            log.info("Not sms captcha loginType, exit.");
            // 其它调用父类默认实现的密码方式登录
            super.additionalAuthenticationChecks(userDetails, authentication);
        }

        log.info("Authenticated sms captcha.");
    }
}
