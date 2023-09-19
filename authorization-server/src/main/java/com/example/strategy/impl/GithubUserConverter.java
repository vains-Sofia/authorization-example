package com.example.strategy.impl;

import com.example.model.security.BasicOAuth2User;
import com.example.strategy.Oauth2UserConverterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import static com.example.constant.SecurityConstants.THIRD_LOGIN_GITHUB;

/**
 * 转换通过Github登录的用户信息
 *
 * @author vains
 */
@RequiredArgsConstructor
@Component(THIRD_LOGIN_GITHUB)
public class GithubUserConverter implements Oauth2UserConverterStrategy {

    private final GiteeUserConverter userConverter;

    protected static final String LOGIN_TYPE = THIRD_LOGIN_GITHUB;

    @Override
    public BasicOAuth2User convert(OAuth2User oAuth2User) {
        // github与gitee目前所取字段一致，直接调用gitee的解析
        BasicOAuth2User basicOauth2User = userConverter.convert(oAuth2User);
        // 提取location
        Object location = oAuth2User.getAttributes().get("location");
        basicOauth2User.setLocation(location + "");
        // 设置登录类型
        basicOauth2User.setType(LOGIN_TYPE);
        return basicOauth2User;
    }
}
