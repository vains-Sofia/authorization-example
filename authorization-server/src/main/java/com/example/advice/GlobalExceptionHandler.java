package com.example.advice;

import com.example.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author vains
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e, HttpServletRequest request) {
        log.error("接口[{}]调用失败，原因：{}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(e.getMessage());
    }

}
