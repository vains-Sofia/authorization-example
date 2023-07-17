package com.example.strategy.impl;

import com.example.entity.Oauth2ThirdAccount;
import com.example.strategy.Oauth2UserConverterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import static com.example.constant.SecurityConstants.THIRD_LOGIN_GITHUB;
import static com.example.strategy.impl.GithubUserConverter.LOGIN_TYPE;

/**
 * 转换通过Github登录的用户信息
 *
 * @author vains
 */
@Component(LOGIN_TYPE)
@RequiredArgsConstructor
public class GithubUserConverter implements Oauth2UserConverterStrategy {

    private final GiteeUserConverter userConverter;

    protected static final String LOGIN_TYPE = THIRD_LOGIN_GITHUB;

    @Override
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // github与gitee目前所取字段一致，直接调用gitee的解析
        Oauth2ThirdAccount thirdAccount = userConverter.convert(oAuth2User);
        thirdAccount.setType(LOGIN_TYPE);
        return thirdAccount;
    }
}
