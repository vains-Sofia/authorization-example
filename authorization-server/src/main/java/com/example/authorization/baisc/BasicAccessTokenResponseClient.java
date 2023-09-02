package com.example.authorization.baisc;

import com.example.authorization.wechat.WechatCodeGrantRequestEntityConverter;
import com.example.authorization.wechat.WechatMapAccessTokenResponseConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 目前额外支持“text/plain”类型数据响应处理类
 *
 * @author vains
 */
@Component
public class BasicAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    /**
     * 默认使用DefaultAuthorizationCodeTokenResponseClient来获取token
     */
    private final DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

    /**
     * 初始化时设置 DefaultAuthorizationCodeTokenResponseClient 支持的响应数据格式
     *  默认添加“text/plain”类型的格式
     */
    public BasicAccessTokenResponseClient() {
        tokenResponseClient.setRequestEntityConverter(new WechatCodeGrantRequestEntityConverter());
        // 自定义 RestTemplate，适配微信登录获取token
        OAuth2AccessTokenResponseHttpMessageConverter messageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>(messageConverter.getSupportedMediaTypes());
        // 微信获取token时响应的类型为“text/plain”，这里特殊处理一下
        mediaTypes.add(MediaType.TEXT_PLAIN);
        messageConverter.setAccessTokenResponseConverter(new WechatMapAccessTokenResponseConverter());
        messageConverter.setSupportedMediaTypes(mediaTypes);

        // 初始化RestTemplate
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(),
                messageConverter));

        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        tokenResponseClient.setRestOperations(restTemplate);
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        return tokenResponseClient.getTokenResponse(authorizationGrantRequest);
    }
}
