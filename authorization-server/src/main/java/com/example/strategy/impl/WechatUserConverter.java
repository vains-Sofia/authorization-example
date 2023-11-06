package com.example.strategy.impl;

import com.example.model.security.BasicOAuth2User;
import com.example.strategy.Oauth2UserConverterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.constant.SecurityConstants.THIRD_LOGIN_WECHAT;

/**
 * 微信用户信息转换器
 *
 * @author vains
 */
@RequiredArgsConstructor
@Component(THIRD_LOGIN_WECHAT)
public class WechatUserConverter implements Oauth2UserConverterStrategy {

    @Override
    public BasicOAuth2User convert(OAuth2User oAuth2User) {
        // 获取三方用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 转换至 BasicOAuth2User
        BasicOAuth2User basicOauth2User = new BasicOAuth2User(oAuth2User, null);
        basicOauth2User.setUniqueId(String.valueOf(attributes.get("openid")));
        basicOauth2User.setThirdUsername(oAuth2User.getName());
        basicOauth2User.setType(THIRD_LOGIN_WECHAT);
        basicOauth2User.setLocation(attributes.get("province")+ " " + attributes.get("city"));
        // 设置基础用户信息
        basicOauth2User.setName(oAuth2User.getName());
        basicOauth2User.setAvatarUrl(String.valueOf(attributes.get("headimgurl")));
        return basicOauth2User;
    }
}