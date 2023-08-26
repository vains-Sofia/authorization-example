package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 测试接口
 *
 * @author vains
 */
@RestController
public class TestController {

    @GetMapping("/test01")
    @PreAuthorize("hasAnyAuthority('message.write')")
    public Mono<String> test01() {
        return Mono.just("test01");
    }

    @GetMapping("/test02")
    @PreAuthorize("hasAnyAuthority('test02')")
    public Mono<String> test02() {
        return Mono.just("test02");
    }

    @GetMapping("/app")
    @PreAuthorize("hasAnyAuthority('app')")
    public Mono<String> app() {
        return Mono.just("app");
    }

}
