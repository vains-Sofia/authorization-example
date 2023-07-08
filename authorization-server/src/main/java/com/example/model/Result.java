package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 公共响应类
 *
 * @author vains
 * @date 2021/3/10 14:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 接口是否处理成功
     */
    private Boolean success;

    /**
     * 接口响应时携带的数据
     */
    private T data;

    /**
     * 操作成功携带数据
     * @param data 数据
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), ("操作成功."),Boolean.TRUE, data);
    }

    /**
     * 操作成功不带数据
     * @return 返回统一响应
     */
    public static Result<String> success() {
        return new Result<>(HttpStatus.OK.value(), ("操作成功."), Boolean.TRUE, (null));
    }

    /**
     * 操作成功携带数据
     * @param message 成功提示消息
     * @param data 成功携带数据
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(HttpStatus.OK.value(), message, Boolean.TRUE, data);
    }

    /**
     * 操作失败返回
     * @param message 成功提示消息
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, Boolean.FALSE, (null));
    }

    /**
     * 操作失败返回
     * @param code 错误码
     * @param message 成功提示消息
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, Boolean.FALSE, (null));
    }

    /**
     * oauth2 问题
     * @param message 失败提示消息
     * @param data 具体的错误信息
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> oauth2Error(Integer code, String message, T data) {
        return new Result<>(code, message, Boolean.FALSE, data);
    }

    /**
     * oauth2 问题
     * @param message 失败提示消息
     * @param data 具体的错误信息
     * @param <T> 类型
     * @return 返回统一响应
     */
    public static <T> Result<T> oauth2Error(String message, T data) {
        return new Result<>(HttpStatus.UNAUTHORIZED.value(), message, Boolean.FALSE, data);
    }

}