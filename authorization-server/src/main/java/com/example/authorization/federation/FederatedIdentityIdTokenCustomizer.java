package com.example.authorization.federation;

import com.example.constant.SecurityConstants;
import com.example.entity.Oauth2BasicUser;
import com.example.model.security.BasicOAuth2User;
import com.example.model.security.BasicOidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import static com.example.constant.SecurityConstants.TOKEN_UNIQUE_ID;
import static com.example.constant.SecurityConstants.OAUTH_LOGIN_TYPE;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link OAuth2TokenCustomizer} to map claims from a federated identity to
 * the {@code id_token} produced by this authorization server.
 *
 * @author vains
 */
public final class FederatedIdentityIdTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private static final Set<String> ID_TOKEN_CLAIMS = Set.of(
            IdTokenClaimNames.ISS,
            IdTokenClaimNames.SUB,
            IdTokenClaimNames.AUD,
            IdTokenClaimNames.EXP,
            IdTokenClaimNames.IAT,
            IdTokenClaimNames.AUTH_TIME,
            IdTokenClaimNames.NONCE,
            IdTokenClaimNames.ACR,
            IdTokenClaimNames.AMR,
            IdTokenClaimNames.AZP,
            IdTokenClaimNames.AT_HASH,
            IdTokenClaimNames.C_HASH
    );

    @Override
    public void customize(JwtEncodingContext context) {
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal());
            context.getClaims().claims(existingClaims -> {
                // Remove conflicting claims set by this authorization server
                existingClaims.keySet().forEach(thirdPartyClaims::remove);

                // Remove standard id_token claims that could cause problems with clients
                ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);

                // Add all other claims directly to id_token
                existingClaims.putAll(thirdPartyClaims);
            });
        }

        // 检查登录用户信息是不是OAuth2User，在token中添加loginType属性
        if (context.getPrincipal().getPrincipal() instanceof BasicOAuth2User user) {
            JwtClaimsSet.Builder claims = context.getClaims();
            // 同时检验是否为String和是否不为空
            claims.claim(OAUTH_LOGIN_TYPE, user.getType());
            claims.claim(TOKEN_UNIQUE_ID, user.getUniqueId());

            // 获取用户的权限
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            transferToContext(authorities, context);
        }

        // 检查登录用户信息是不是UserDetails，排除掉没有用户参与的流程
        if (context.getPrincipal().getPrincipal() instanceof Oauth2BasicUser user) {
            JwtClaimsSet.Builder claims = context.getClaims();
            // 获取用户的权限
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            claims.claim(TOKEN_UNIQUE_ID, user.getId());
            transferToContext(authorities, context);
        }
    }

    /**
     * 从认证信息中提取 Claims
     *
     * @param principal 当前登录用户认证信息
     * @return 当前用户的信息
     */
    private Map<String, Object> extractClaims(Authentication principal) {
        Map<String, Object> claims;
        if (principal.getPrincipal() instanceof BasicOidcUser oidcUser) {
            OidcIdToken idToken = oidcUser.getIdToken();
            claims = idToken.getClaims();
        } else if (principal.getPrincipal() instanceof BasicOAuth2User oAuth2User) {
            claims = oAuth2User.getAttributes();
        } else {
            claims = Collections.emptyMap();
        }

        return new HashMap<>(claims);
    }

    public void transferToContext(Collection<? extends GrantedAuthority> authorities, JwtEncodingContext context) {
        // 获取申请的scopes
        Set<String> scopes = context.getAuthorizedScopes();
        // 提取权限并转为字符串
        Set<String> authoritySet = Optional.ofNullable(authorities).orElse(Collections.emptyList()).stream()
                    // 获取权限字符串
                    .map(GrantedAuthority::getAuthority)
                    // 去重
                    .collect(Collectors.toSet());

        // 合并scope与用户信息
        authoritySet.addAll(scopes);

        JwtClaimsSet.Builder claims = context.getClaims();
        // 将权限信息放入jwt的claims中（也可以生成一个以指定字符分割的字符串放入）
        claims.claim(SecurityConstants.AUTHORITIES_KEY, authoritySet);
        // 放入其它自定内容
        // 角色、头像...
    }

}
