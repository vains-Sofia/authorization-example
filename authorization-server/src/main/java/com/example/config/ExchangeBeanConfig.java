package com.example.config;

import com.example.exchange.GiteeExchange;
import com.example.exchange.ProjectExchange;
import com.example.property.CustomSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Http Interface注入ioc配置
 *
 * @author vains
 */
@Configuration
@RequiredArgsConstructor
public class ExchangeBeanConfig {

    private final CustomSecurityProperties securityProperties;

    /**
     * 注入MineExchange
     *
     * @return MineExchange
     */
    @Bean
    public ProjectExchange mineExchange() {
        WebClient webClient = WebClient.builder().baseUrl(securityProperties.getIssuerUrl()).build();
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                        .build();
        return httpServiceProxyFactory.createClient(ProjectExchange.class);
    }

    /**
     * 注入Gitee Exchange
     *
     * @return GiteeExchange
     */
    @Bean
    public GiteeExchange giteeExchange() {
        String giteeUrl = "https://gitee.com";
        WebClient webClient = WebClient.builder().baseUrl(giteeUrl).build();
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                        .build();
        return httpServiceProxyFactory.createClient(GiteeExchange.class);
    }

}