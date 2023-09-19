package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.constant.SecurityConstants;
import com.example.entity.Oauth2BasicUser;
import com.example.entity.Oauth2ThirdAccount;
import com.example.entity.SysAuthority;
import com.example.model.Result;
import com.example.model.response.Oauth2UserinfoResult;
import com.example.model.security.CustomGrantedAuthority;
import com.example.property.CustomSecurityProperties;
import com.example.service.IOauth2BasicUserService;
import com.example.service.IOauth2ThirdAccountService;
import com.example.service.ISysAuthorityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.constant.SecurityConstants.*;

/**
 * 认证服务器相关自定接口
 *
 * @author vains
 */
@Controller
@RequiredArgsConstructor
public class AuthorizationController {

    private final ISysAuthorityService authorityService;

    private final IOauth2BasicUserService basicUserService;

    private final IOauth2ThirdAccountService thirdAccountService;

    private final CustomSecurityProperties customSecurityProperties;

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationConsentService authorizationConsentService;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @ResponseBody
    @GetMapping("/user")
    public Oauth2UserinfoResult user() {
        Oauth2UserinfoResult result = new Oauth2UserinfoResult();

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 其它非token方式获取用户信息
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            BeanUtils.copyProperties(authentication.getPrincipal(), result);
            result.setSub(authentication.getName());
            return result;
        }

        // 获取jwt解析内容
        Jwt token = jwtAuthenticationToken.getToken();

        // 获取当前登录类型
        String loginType = token.getClaim(OAUTH_LOGIN_TYPE);
        // 获取用户唯一Id
        String uniqueId = token.getClaimAsString(TOKEN_UNIQUE_ID);
        // 基础用户信息id
        Integer basicUserId = null;

        // 获取scope
        List<String> scopes = token.getClaimAsStringList(OAuth2TokenIntrospectionClaimNames.SCOPE);
        // 获取Token中的权限列表
        List<String> claimAsStringList = token.getClaimAsStringList(SecurityConstants.AUTHORITIES_KEY);

        // 如果登录类型不为空则代表是三方登录，获取三方用户信息
        if (!ObjectUtils.isEmpty(loginType)) {
            // 根据三方登录类型与三方用户的唯一Id查询用户信息
            LambdaQueryWrapper<Oauth2ThirdAccount> wrapper = Wrappers.lambdaQuery(Oauth2ThirdAccount.class)
                .eq(Oauth2ThirdAccount::getUniqueId, uniqueId)
                .eq(Oauth2ThirdAccount::getType, loginType);
            Oauth2ThirdAccount oauth2ThirdAccount = thirdAccountService.getOne(wrapper);
            if (oauth2ThirdAccount != null) {
                basicUserId = oauth2ThirdAccount.getUserId();
                // 复制三方用户信息
                BeanUtils.copyProperties(oauth2ThirdAccount, result);
            }
        } else {
            // 为空则代表是使用当前框架提供的登录接口登录的，转为基础用户信息
            basicUserId = Integer.parseInt(uniqueId);
        }

        if (basicUserId == null) {
            // 如果用户id为空，代表获取三方用户信息失败
            result.setSub(authentication.getName());
            return result;
        }

        // 查询基础用户信息
        Oauth2BasicUser basicUser = basicUserService.getById(basicUserId);
        BeanUtils.copyProperties(basicUser, result);

        // 填充权限信息
        if (ObjectUtils.isEmpty(claimAsStringList)) {
            // 如果获取不到权限信息去数据库查
            List<SysAuthority> sysAuthorities = authorityService.getByUserId(basicUser.getId());
            Set<CustomGrantedAuthority> authorities = sysAuthorities.stream()
                    .map(SysAuthority::getAuthority)
                    .map(CustomGrantedAuthority::new)
                    .collect(Collectors.toSet());
            if (!ObjectUtils.isEmpty(scopes)) {
                scopes.stream().map(CustomGrantedAuthority::new).forEach(authorities::add);
            }
            result.setAuthorities(authorities);
        } else {
            Set<CustomGrantedAuthority> authorities = claimAsStringList.stream()
                    .map(CustomGrantedAuthority::new)
                    .collect(Collectors.toSet());
            // 否则设置为token中获取的
            result.setAuthorities(authorities);
        }

        result.setSub(authentication.getName());
        return result;
    }

    @GetMapping("/activate")
    public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
        if (userCode != null) {
            return "redirect:/oauth2/device_verification?user_code=" + userCode;
        }
        return "device-activate";
    }

    @GetMapping("/activate/redirect")
    public String activateRedirect(HttpSession session,
                                   @RequestParam(value = "user_code", required = false) String userCode) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(customSecurityProperties.getDeviceActivateUri())
                .queryParam("userCode", userCode)
                .queryParam(NONCE_HEADER_NAME, session.getId());
        return "redirect:" + uriBuilder.build(Boolean.TRUE).toUriString();
    }

    @GetMapping("/activated")
    public String activated() {
        return "device-activated";
    }

    @GetMapping(value = "/", params = "success")
    public String success() {
        return "device-activated";
    }

    @ResponseBody
    @GetMapping(value = "/")
    public String index() {
        return """
            <h3>根目录什么都没有，试着发起授权申请流程吧</h3>
            简单说下为什么会跳转到根目录，大概有以下几种情况：<br>
            <ol>
                <li>最一开始直接访问认证服务的跟目录，被项目重定向到登录页面(前后端不分离)，在登录页面登录成功后请求会到这里</li>
                <li>直接访问登录页面(前后端不分离)，在登录页面登录成功后请求会到这里</li>
                <li>前后端分离的情况下直接访问前端的登录页面，使用三方登录成功后会被重定向到这里</li>
            </ol>
            Security有一个机制，当请求被重定向到登录页面时会存储这个请求，在登录成功后从缓存中获取这个请求并重定向到该请求，从缓存中获取请求失败后会默认重定向到根目录，
            所以上边第2中情况会重定向到根目录；<br>
            再来说一下第3种情况，因为认证服务集成了联合身份认证，三方应用的授权申请都由认证服务发起，包括回调地址也由认证服务处理，
            所以三方登录成功后它的回调地址是认证服务，认证服务通过授权码获取到认证信息(三方应用登录的accessToken和用户信息)时会有一个跳转，跳转的逻辑和上边的一样，都是
            从缓存中获取跳转登录之前存储的地址，如果是走认证服务的授权申请流程就是(/oauth2/authorize)接口，但是如果直接访问前端登录页面在通过三方登录就获取不到缓存中的
            地址，所以默认就跳转到根目录了。<br>
            如果还有别的情况也可以继续补充。
        """;
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        Object attribute = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (attribute instanceof AuthenticationException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        return "login";
    }

    @GetMapping(value = "/oauth2/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                          @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

        // 获取consent页面所需的参数
        Map<String, Object> consentParameters = getConsentParameters(scope, state, clientId, userCode, principal);
        // 转至model中，让框架渲染页面
        consentParameters.forEach(model::addAttribute);

        return "consent";
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping(value = "/oauth2/consent/redirect")
    public Result<String> consentRedirect(HttpSession session,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                          @RequestHeader(name = NONCE_HEADER_NAME, required = false) String nonceId,
                                          @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

        // 携带当前请求参数与nonceId重定向至前端页面
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(customSecurityProperties.getConsentPageUri())
                .queryParam(OAuth2ParameterNames.SCOPE, UriUtils.encode(scope, StandardCharsets.UTF_8))
                .queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(state, StandardCharsets.UTF_8))
                .queryParam(OAuth2ParameterNames.CLIENT_ID, clientId)
                .queryParam(OAuth2ParameterNames.USER_CODE, userCode)
                .queryParam(NONCE_HEADER_NAME, ObjectUtils.isEmpty(nonceId) ? session.getId() : nonceId);

        String uriString = uriBuilder.build(Boolean.TRUE).toUriString();
        if (ObjectUtils.isEmpty(userCode) || !UrlUtils.isAbsoluteUrl(customSecurityProperties.getDeviceActivateUri())) {
            // 不是设备码模式或者设备码验证页面不是前后端分离的，无需返回json，直接重定向
            redirectStrategy.sendRedirect(request, response, uriString);
            return null;
        }
        // 兼容设备码，需响应JSON，由前端进行跳转
        return Result.success(uriString);
    }

    @ResponseBody
    @GetMapping(value = "/oauth2/consent/parameters")
    public Result<Map<String, Object>> consentParameters(Principal principal,
                                                         @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                                         @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                                         @RequestParam(OAuth2ParameterNames.STATE) String state,
                                                         @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

        // 获取consent页面所需的参数
        Map<String, Object> consentParameters = getConsentParameters(scope, state, clientId, userCode, principal);

        return Result.success(consentParameters);
    }

    /**
     * 根据授权确认相关参数获取授权确认与未确认的scope相关参数
     *
     * @param scope     scope权限
     * @param state     state
     * @param clientId  客户端id
     * @param userCode  设备码授权流程中的用户码
     * @param principal 当前认证信息
     * @return 页面所需数据
     */
    private Map<String, Object> getConsentParameters(String scope,
                                                     String state,
                                                     String clientId,
                                                     String userCode,
                                                     Principal principal) {
        // Remove scopes that were already approved
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new RuntimeException("客户端不存在");
        }
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        Map<String, Object> parameters = new HashMap<>(7);
        parameters.put("clientId", registeredClient.getClientId());
        parameters.put("clientName", registeredClient.getClientName());
        parameters.put("state", state);
        parameters.put("scopes", withDescription(scopesToApprove));
        parameters.put("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
        parameters.put("principalName", principal.getName());
        parameters.put("userCode", userCode);
        if (StringUtils.hasText(userCode)) {
            parameters.put("requestURI", "/oauth2/device_verification");
        } else {
            parameters.put("requestURI", "/oauth2/authorize");
        }
        return parameters;
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));

        }
        return scopeWithDescriptions;
    }

    @Data
    public static class ScopeWithDescription {
        private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(
                    OidcScopes.PROFILE,
                    "This application will be able to read your profile information."
            );
            scopeDescriptions.put(
                    "message.read",
                    "This application will be able to read your message."
            );
            scopeDescriptions.put(
                    "message.write",
                    "This application will be able to add new messages. It will also be able to edit and delete existing messages."
            );
            scopeDescriptions.put(
                    "other.scope",
                    "This is another scope example of a scope description."
            );
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
        }
    }

}