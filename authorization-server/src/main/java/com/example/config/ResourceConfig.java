package com.example.config;

import com.example.authorization.baisc.BasicAccessTokenResponseClient;
import com.example.authorization.baisc.BasicAuthorizationRequestResolver;
import com.example.authorization.handler.LoginFailureHandler;
import com.example.authorization.handler.LoginSuccessHandler;
import com.example.support.RedisSecurityContextRepository;
import com.example.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.filter.CorsFilter;

import static com.example.constant.SecurityConstants.LOGIN_URL;

/**
 * 资源服务器配置
 * <p>
 * {@link EnableMethodSecurity} 开启全局方法认证，启用JSR250注解支持，启用注解 {@link Secured} 支持，
 * 在Spring Security 6.0版本中将@Configuration注解从@EnableWebSecurity, @EnableMethodSecurity, @EnableGlobalMethodSecurity
 * 和 @EnableGlobalAuthentication 中移除，使用这些注解需手动添加 @Configuration 注解
 * {@link EnableWebSecurity} 注解有两个作用:
 * 1. 加载了WebSecurityConfiguration配置类, 配置安全认证策略。
 * 2. 加载了AuthenticationConfiguration, 配置了认证信息。
 *
 * @author vains
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class ResourceConfig {

    private final CorsFilter corsFilter;

    private final BasicAccessTokenResponseClient accessTokenResponseClient;

    private final RedisSecurityContextRepository redisSecurityContextRepository;

    private final BasicAuthorizationRequestResolver authorizationRequestResolver;

    /**
     * 不需要认证即可访问的路径
     */
    private final String[] ignoreUrls = {"/assets/**", "/webjars/**", "/login", "/getCaptcha", "/getSmsCaptcha", "/error", "/oauth2/consent/parameters"};

    /**
     * 配置认证相关的过滤器链(资源服务，客户端配置)
     *
     * @param http spring security核心配置类
     * @return 过滤器链
     * @throws Exception 抛出
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 添加基础的认证配置
        SecurityUtils.applyBasicSecurity(http, redisSecurityContextRepository, corsFilter);

        http.authorizeHttpRequests((authorize) -> authorize
                        // 放行静态资源和不需要认证的url
                        .requestMatchers(ignoreUrls).permitAll()
                        .anyRequest().authenticated()
                )
                // 指定登录页面
                .formLogin(formLogin -> {
                            formLogin.loginPage("/login");
                            if (UrlUtils.isAbsoluteUrl(LOGIN_URL)) {
                                // 绝对路径代表是前后端分离，登录成功和失败改为写回json，不重定向了
                                formLogin.successHandler(new LoginSuccessHandler());
                                formLogin.failureHandler(new LoginFailureHandler());
                            }
                        }
                );

        // 联合身份认证
        http.oauth2Login(oauth2Login -> oauth2Login
                .loginPage(LOGIN_URL)
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestResolver(this.authorizationRequestResolver)
                )
                .tokenEndpoint(token -> token
                        .accessTokenResponseClient(this.accessTokenResponseClient)
                )
        );

        return http.build();
    }

}
