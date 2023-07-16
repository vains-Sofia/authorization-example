package com.example.strategy.impl;

import com.example.entity.Oauth2ThirdAccount;
import com.example.strategy.Oauth2UserConverterStrategy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import static com.example.strategy.impl.GithubUserConverter.LOGIN_TYPE;

/**
 * 转换通过Github登录的用户信息
 *
 * @author vains
 */
@Component(LOGIN_TYPE)
public class GithubUserConverter implements Oauth2UserConverterStrategy {

    protected static final String LOGIN_TYPE = "github";

    @Override
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // TODO 映射GitHub的用户信息
        System.out.println(oAuth2User.getAttributes());
        return null;
    }
}
