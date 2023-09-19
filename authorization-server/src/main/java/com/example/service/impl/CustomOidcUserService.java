package com.example.service.impl;

import com.example.model.security.BasicOAuth2User;
import com.example.model.security.BasicOidcUser;
import com.example.service.IOauth2ThirdAccountService;
import com.example.strategy.context.Oauth2UserConverterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 自定义三方oidc登录用户信息服务
 *
 * @author vains
 */
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final IOauth2ThirdAccountService thirdAccountService;

    private final Oauth2UserConverterContext userConverterContext;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取三方用户信息
        OidcUser oidcUser = super.loadUser(userRequest);
        // 转为项目中的三方用户信息
        BasicOAuth2User basicOauth2User = userConverterContext.convert(userRequest, oidcUser);
        // 检查用户信息
        thirdAccountService.checkAndSaveUser(basicOauth2User);

        // 重新生成oidcUser
        if (StringUtils.hasText(basicOauth2User.getNameAttributeKey())) {
            return new BasicOidcUser(oidcUser, oidcUser.getIdToken(), oidcUser.getUserInfo(), basicOauth2User.getNameAttributeKey());
        }
        return new BasicOidcUser(oidcUser, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
