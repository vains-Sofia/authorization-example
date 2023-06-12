package com.example.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常类
 *  校验验证码异常时抛出
 *
 * @author vains
 */
public class InvalidCaptchaException extends AuthenticationException {

    public InvalidCaptchaException(String msg) {
        super(msg);
    }

}