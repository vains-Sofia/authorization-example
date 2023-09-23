package com.example.model.response.gitee;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取邮箱响应
 *
 * @author vains
 */
@Data
public class GetEmailResult implements Serializable {

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 状态
     */
    private String state;

    /**
     * 权限
     */
    private List<String> scope;

}
