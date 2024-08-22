package com.example.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * jwk 接口
 *
 * @author vains
 */
@RestController
@RequiredArgsConstructor
public class JwkSetController {

    private final RSAKey rsaKey;

    @GetMapping("/jwkSet")
    public String jwkSet() {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return jwkSet.toString(Boolean.TRUE);
    }

}
