package com.example.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 基础 OIDC 用户信息
 *
 * @author vains
 */
@Data
@JsonSerialize
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicOidcUser extends BasicOAuth2User implements OidcUser {
    
    /**
     * 用户账号
     */
    private String sub;

    /**
     * OIDC 请求中的idToken
     */
    private OidcIdToken idToken;

    /**
     * 用户信息
     */
    private OidcUserInfo userInfo;
    
    public BasicOidcUser(OAuth2User oAuth2User, OidcIdToken idToken, OidcUserInfo userInfo, String nameAttributeKey) {
        super(oAuth2User, nameAttributeKey);
        this.idToken = idToken;
        this.userInfo = userInfo;
    }
    
    public BasicOidcUser(OAuth2User oAuth2User, OidcIdToken idToken, OidcUserInfo userInfo) {
        this(oAuth2User, idToken, userInfo, IdTokenClaimNames.SUB);
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.getAttributes();
    }
}