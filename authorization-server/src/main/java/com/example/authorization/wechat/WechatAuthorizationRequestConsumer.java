package com.example.authorization.wechat;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Objects;
import java.util.function.Consumer;

import static com.example.constant.SecurityConstants.*;

/**
 * 自定义微信登录认证请求
 *
 * @author vains
 */
public class WechatAuthorizationRequestConsumer implements Consumer<OAuth2AuthorizationRequest.Builder> {

    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder) {
        OAuth2AuthorizationRequest authorizationRequest = builder.build();
        Object registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        if (Objects.equals(registrationId, THIRD_LOGIN_WECHAT)) {
            // 判断是否微信登录，如果是微信登录则将appid添加至请求参数中
            builder.additionalParameters((params) -> params.put(WECHAT_PARAMETER_FORCE_POPUP, true));
            builder.additionalParameters((params) -> params.put(WECHAT_PARAMETER_APPID, authorizationRequest.getClientId()));

            builder.parameters(params -> {
                params.clear();

                // 重置授权申请参数顺序
                params.put(WECHAT_PARAMETER_APPID, authorizationRequest.getClientId());
                params.put(OAuth2ParameterNames.REDIRECT_URI, authorizationRequest.getRedirectUri());
                params.put(OAuth2ParameterNames.RESPONSE_TYPE, authorizationRequest.getResponseType().getValue());
                params.put(OAuth2ParameterNames.SCOPE, String.join(" ", authorizationRequest.getScopes()));
                params.put(OAuth2ParameterNames.STATE, authorizationRequest.getState());
                params.put(OAuth2ParameterNames.CLIENT_ID, authorizationRequest.getClientId());
                params.put(WECHAT_PARAMETER_FORCE_POPUP, true);
            });
        }
    }

}
