package com.example.config;

import com.example.exchange.ProjectExchange;
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
public class ExchangeBeanConfig {

    /**
     * 注入MineExchange
     *
     * @return MineExchange
     */
    @Bean
    public ProjectExchange mineExchange() {
        WebClient webClient = WebClient.builder().baseUrl("http://127.0.0.1:8080").build();
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                        .build();
        return httpServiceProxyFactory.createClient(ProjectExchange.class);
    }

}