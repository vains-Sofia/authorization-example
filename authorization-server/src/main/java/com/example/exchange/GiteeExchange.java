package com.example.exchange;

import com.example.model.response.gitee.GetEmailResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * gitee接口
 *
 * @author vains
 */
@Component
@HttpExchange(GiteeExchange.API_PATH)
public interface GiteeExchange {

    String API_PATH = "/api/v5";

    /**
     * 根据三方登录的token获取Gitee用户的邮箱
     *
     * @param accessToken gitee登录获取到的AccessToken
     * @return Gitee用户的邮箱
     */
    @GetExchange("/emails")
    List<GetEmailResult> emails(@RequestParam("access_token") String accessToken);

}
