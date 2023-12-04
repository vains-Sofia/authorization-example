package com.example.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.ObjectUtils;

/**
 * 同时支持匿名token与jwt token配置
 *
 * @author vains
 */
@Configuration(proxyBeanMethods = false)
public class BothJwtAndOpaqueTokenSupportConfig {


    /**
     * 根据jwtDecoder和令牌自省生成{@link AuthenticationManagerResolver }，在AuthenticationManagerResolver中根据当前请求决定使用jwt解析器还是去token自省端点获取当前token信息
     *
     * @param jwtDecoder              jwt解析器
     * @param opaqueTokenIntrospector token自省
     * @return 返回 {@link AuthenticationManagerResolver }
     */
    @Bean
    AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver
    (JwtDecoder jwtDecoder, OpaqueTokenIntrospector opaqueTokenIntrospector) {
        AuthenticationManager jwt = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
        AuthenticationManager opaqueToken = new ProviderManager(
                new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector));
        return (request) -> useJwt(request) ? jwt : opaqueToken;
    }

    /**
     * 判断请求头是否有key ： token-type，有值不是jwt
     * 这里根据自己业务实现，可以获取token后再判断token是jwt还是匿名token
     *
     * @param request 请求对象
     * @return 是否使用jwt token
     */
    private boolean useJwt(HttpServletRequest request) {
        return ObjectUtils.isEmpty(request.getHeader("token-type"));
    }

}