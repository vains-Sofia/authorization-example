package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 资源服务同时支持匿名token和Jwt token集成
 *
 * @author vains
 */
@SpringBootApplication
public class BothJwtAndOpaqueTokenResourceExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BothJwtAndOpaqueTokenResourceExampleApplication.class, args);
    }

}