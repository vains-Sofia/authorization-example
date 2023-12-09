package com.example.model.response.qrcode;

import lombok.Data;

import java.util.Set;

/**
 * web前端轮询二维码状态出参
 *
 * @author vains
 */
@Data
public class QrCodeLoginFetchResponse {

    /**
     * 二维码状态
     * 0:待扫描，1:已扫描，2:已确认
     */
    private Integer qrCodeStatus;

    /**
     * 是否已过期
     */
    private Boolean expired;

    /**
     * 扫描人头像
     */
    private String avatarUrl;

    /**
     * 扫描人昵称
     */
    private String name;

    /**
     * 待确认scope
     */
    private Set<String> scopes;

    /**
     * 跳转登录之前请求的接口
     */
    private String beforeLoginRequestUri;

    /**
     * 跳转登录之前请求参数
     */
    private String beforeLoginQueryString;

}