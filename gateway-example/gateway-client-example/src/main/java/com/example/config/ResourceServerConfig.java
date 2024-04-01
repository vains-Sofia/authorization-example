package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 资源服务器配置
 *
 * @author vains
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = false)
public class ResourceServerConfig {

    /**
     * 配置认证相关的过滤器链
     *
     * @param http Spring Security的核心配置类
     * @return 过滤器链
     */
    @Bean
    public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // 禁用csrf与cors
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(ServerHttpSecurity.CorsSpec::disable);

        // 开启全局验证
        http.authorizeExchange((authorize) -> authorize
                .pathMatchers("/actuator/**").permitAll()
                //全部需要认证
                .anyExchange().authenticated()
        );

        // 开启OAuth2登录
        http.oauth2Login(Customizer.withDefaults());

        // 设置当前服务为资源服务，解析请求头中的token
        http.oauth2ResourceServer((resourceServer) -> resourceServer
                        // 使用jwt
                        .jwt(jwt -> jwt
                                // 请求中携带token访问时会触发该解析器适配器
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
        );

        return http.build();
    }

    /**
     * 自定义jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
     *
     * @return jwt解析器适配器 ReactiveJwtAuthenticationConverterAdapter
     */
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // 设置权限信息在jwt claims中的key
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}