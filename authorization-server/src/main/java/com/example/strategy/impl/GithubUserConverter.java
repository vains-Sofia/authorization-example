package com.example.strategy.impl;

import com.example.entity.Oauth2ThirdAccount;
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
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // github与gitee目前所取字段一致，直接调用gitee的解析
        Oauth2ThirdAccount thirdAccount = userConverter.convert(oAuth2User);
        // 提取location
        Object location = oAuth2User.getAttributes().get("location");
        thirdAccount.setLocation(String.valueOf(location));
        // 设置登录类型
        thirdAccount.setType(LOGIN_TYPE);
        return thirdAccount;
    }
}