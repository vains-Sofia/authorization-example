package com.example.authorization.baisc;

import com.example.authorization.wechat.WechatAuthorizationRequestConsumer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/**
 * 基础授权请求处理类，目前只做了微信登录授权请求的适配
 *
 * @author vains
 */
@Component
public class BasicAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public BasicAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        // DI通过构造器自动注入clientRegistrationRepository，实例化DefaultOAuth2AuthorizationRequestResolver处理
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        // 兼容微信登录授权申请
        this.authorizationRequestResolver.setAuthorizationRequestCustomizer(new WechatAuthorizationRequestConsumer());
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return this.authorizationRequestResolver.resolve(request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return this.authorizationRequestResolver.resolve(request, clientRegistrationId);
    }
}
