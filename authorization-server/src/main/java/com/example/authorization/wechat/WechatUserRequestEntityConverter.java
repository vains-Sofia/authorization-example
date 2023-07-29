package com.example.authorization.wechat;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.example.constant.SecurityConstants.THIRD_LOGIN_WECHAT;
import static com.example.constant.SecurityConstants.WECHAT_PARAMETER_OPENID;

/**
 * 微信登录获取用户信息参数转换器
 *
 * @author vains
 */
public class WechatUserRequestEntityConverter extends OAuth2UserRequestEntityConverter {

    @Override
    public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
        // 获取配置文件中的客户端信息
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        if (THIRD_LOGIN_WECHAT.equals(clientRegistration.getRegistrationId())) {
            // 对于微信登录的特殊处理，请求用户信息时添加openid与access_token参数
            Object openid = userRequest.getAdditionalParameters().get(WECHAT_PARAMETER_OPENID);
            URI uri = UriComponentsBuilder
                    .fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                    .queryParam(WECHAT_PARAMETER_OPENID, openid)
                    .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                    .build().toUri();
            return new RequestEntity<>(HttpMethod.GET, uri);
        }
        return super.convert(userRequest);
    }
}
