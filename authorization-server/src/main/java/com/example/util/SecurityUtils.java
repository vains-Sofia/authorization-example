package com.example.util;

import com.example.authorization.device.DeviceClientAuthenticationConverter;
import com.example.authorization.device.DeviceClientAuthenticationProvider;
import com.example.authorization.handler.ConsentAuthenticationFailureHandler;
import com.example.authorization.handler.ConsentAuthorizationResponseHandler;
import com.example.authorization.handler.DeviceAuthorizationResponseHandler;
import com.example.authorization.handler.LoginTargetAuthenticationEntryPoint;
import com.example.constant.SecurityConstants;
import com.example.property.CustomSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证鉴权工具
 *
 * @author vains
 */
@Slf4j
public class SecurityUtils {

    private static final String CUSTOM_DEVICE_REDIRECT_URI = "/activate/redirect";

    private static final String CUSTOM_CONSENT_REDIRECT_URI = "/oauth2/consent/redirect";

    private SecurityUtils() {
        // 禁止实例化工具类
        throw new UnsupportedOperationException("Utility classes cannot be instantiated.");
    }

    /**
     * 从认证信息中获取客户端token
     *
     * @param authentication 认证信息
     * @return 客户端认证信息，获取失败抛出异常
     */
    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 提取请求中的参数并转为一个map返回
     *
     * @param request 当前请求
     * @return 请求中的参数
     */
    public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            for (String value : values) {
                parameters.add(key, value);
            }
        });
        return parameters;
    }

    /**
     * 抛出 OAuth2AuthenticationException 异常
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param errorUri  错误对照地址
     */
    public static void throwError(String errorCode, String message, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, message, errorUri);
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * 认证与鉴权失败回调
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param e        具体的异常信息
     */
    public static void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Map<String, String> parameters = getErrorParameter(request, response, e);
        String wwwAuthenticate = computeWwwAuthenticateHeaderValue(parameters);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(JsonUtils.objectCovertToJson(parameters));
            response.getWriter().flush();
        } catch (IOException ex) {
            log.error("写回错误信息失败", e);
        }
    }

    /**
     * 获取异常信息map
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param e        本次异常具体的异常实例
     * @return 异常信息map
     */
    private static Map<String, String> getErrorParameter(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
            // 权限不足
            parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
            parameters.put("error_description",
                    "The request requires higher privileges than provided by the access token.");
            parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        if (e instanceof OAuth2AuthenticationException authenticationException) {
            // jwt异常，e.g. jwt超过有效期、jwt无效等
            OAuth2Error error = authenticationException.getError();
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (error instanceof BearerTokenError bearerTokenError) {
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
                response.setStatus(bearerTokenError.getHttpStatus().value());
            }
        }
        if (e instanceof InsufficientAuthenticationException) {
            // 没有携带jwt访问接口，没有客户端认证信息
            parameters.put("error", BearerTokenErrorCodes.INVALID_TOKEN);
            parameters.put("error_description", "Not authorized.");
            parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        parameters.put("message", e.getMessage());
        return parameters;
    }

    /**
     * 生成放入请求头的错误信息
     *
     * @param parameters 参数
     * @return 字符串
     */
    public static String computeWwwAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }

    /**
     * 添加基础认证配置
     * 开启认证服务OIDC配置，禁用 csrf 与 cors，配置认证存储方式，设置跳转至登录页面逻辑，添加资源服务配置
     *
     * @param http       Security核心配置类
     * @param corsFilter 跨域处理过滤器
     */
    @SneakyThrows
    public static void applyBasicSecurity(HttpSecurity http,
                                          CorsFilter corsFilter,
                                          CustomSecurityProperties customSecurityProperties) {
        // 添加跨域过滤器
        http.addFilter(corsFilter);

        OAuth2AuthorizationServerConfigurer httpConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);

        if (httpConfigurer != null) {
            // 认证服务配置
            httpConfigurer
                    // 开启OpenID Connect 1.0协议相关端点
                    .oidc(oidcConfigurer -> oidcConfigurer
                            .providerConfigurationEndpoint(provider -> provider
                                    .providerConfigurationCustomizer(builder -> builder
                                            // 为OIDC端点添加短信认证码的登录方式
                                            .grantType(SecurityConstants.GRANT_TYPE_SMS_CODE)
                                    )
                            )
                    )
                    // 让认证服务器元数据中有自定义的认证方式
                    .authorizationServerMetadataEndpoint(metadata -> metadata.authorizationServerMetadataCustomizer(customizer -> customizer.grantType(SecurityConstants.GRANT_TYPE_SMS_CODE)));
        }

        // 禁用 csrf 与 cors
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http
                // 当未登录时访问认证端点时重定向至login页面
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginTargetAuthenticationEntryPoint(customSecurityProperties.getLoginUrl(), customSecurityProperties.getDeviceActivateUri()),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token
                // 处理使用access token访问用户信息端点和客户端注册端点
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults())
                        .accessDeniedHandler(SecurityUtils::exceptionHandler)
                        .authenticationEntryPoint(SecurityUtils::exceptionHandler));
    }

    /**
     * 添加设备码相关自定义配置
     * 设备码的授权确认页面、验证页面、自定义响应处理等
     *
     * @param http                        核心配置类
     * @param registeredClientRepository  客户端Repository
     * @param authorizationServerSettings 认证服务配置类
     */
    public static void applyDeviceSecurity(HttpSecurity http,
                                           CustomSecurityProperties customSecurityProperties,
                                           RegisteredClientRepository registeredClientRepository,
                                           AuthorizationServerSettings authorizationServerSettings) {
        // 新建设备码converter和provider
        DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
                new DeviceClientAuthenticationConverter(
                        authorizationServerSettings.getDeviceAuthorizationEndpoint());
        DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
                new DeviceClientAuthenticationProvider(registeredClientRepository);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // 设置自定义用户确认授权页
                .authorizationEndpoint(authorizationEndpoint -> {
                            // 校验授权确认页面是否为完整路径；是否是前后端分离的页面
                            boolean absoluteUrl = UrlUtils.isAbsoluteUrl(customSecurityProperties.getConsentPageUri());
                            // 如果是分离页面则重定向，否则转发请求
                            authorizationEndpoint.consentPage(customSecurityProperties.getConsentPageUri());
                            if (absoluteUrl) {
                                // 适配前后端分离的授权确认页面，成功/失败响应json
                                authorizationEndpoint.errorResponseHandler(new ConsentAuthenticationFailureHandler(customSecurityProperties.getConsentPageUri()));
                                authorizationEndpoint.authorizationResponseHandler(new ConsentAuthorizationResponseHandler(customSecurityProperties.getConsentPageUri()));
                            }
                        }
                )
                // 设置设备码用户验证url(自定义用户验证页)
                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
                        deviceAuthorizationEndpoint.verificationUri(UrlUtils.isAbsoluteUrl(customSecurityProperties.getDeviceActivatedUri()) ? CUSTOM_DEVICE_REDIRECT_URI : customSecurityProperties.getDeviceActivateUri())
                )
                // 设置验证设备码用户确认页面
                .deviceVerificationEndpoint(deviceVerificationEndpoint -> {
                            // 校验授权确认页面是否为完整路径；是否是前后端分离的页面
                            boolean absoluteUrl = UrlUtils.isAbsoluteUrl(customSecurityProperties.getConsentPageUri());
                            // 如果是分离页面则重定向，否则转发请求
                            deviceVerificationEndpoint.consentPage(absoluteUrl ? CUSTOM_CONSENT_REDIRECT_URI : customSecurityProperties.getConsentPageUri());
                            if (absoluteUrl) {
                                // 适配前后端分离的授权确认页面，失败响应json
                                deviceVerificationEndpoint.errorResponseHandler(new ConsentAuthenticationFailureHandler(customSecurityProperties.getConsentPageUri()));
                            }
                            // 如果授权码验证页面或者授权确认页面是前后端分离的
                            if (UrlUtils.isAbsoluteUrl(customSecurityProperties.getDeviceActivateUri()) || absoluteUrl) {
                                log.info("授权码验证页面是否为分离页面：{}，授权确认页面是否为分离页面：{}", UrlUtils.isAbsoluteUrl(customSecurityProperties.getDeviceActivateUri()), absoluteUrl);
                                // 添加响应json处理
                                deviceVerificationEndpoint.deviceVerificationResponseHandler(new DeviceAuthorizationResponseHandler(customSecurityProperties.getDeviceActivatedUri()));
                            }
                        }
                )
                .clientAuthentication(clientAuthentication ->
                        // 客户端认证添加设备码的converter和provider
                        clientAuthentication
                                .authenticationConverter(deviceClientAuthenticationConverter)
                                .authenticationProvider(deviceClientAuthenticationProvider)
                );
    }

}