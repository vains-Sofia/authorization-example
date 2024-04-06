package com.example.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.model.Result;
import com.example.model.response.CaptchaResult;
import com.example.support.RedisOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

import static com.example.constant.RedisConstants.*;

/**
 * 登录接口，登录使用的接口
 *
 * @author vains
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final RedisOperator<String> redisOperator;

    @GetMapping("/getSmsCaptcha")
    public Result<String> getSmsCaptcha(String phone) {
        // 示例项目，固定1234
        String smsCaptcha = "666999";
        // 存入缓存中，5分钟后过期
        redisOperator.set((SMS_CAPTCHA_PREFIX_KEY + phone), smsCaptcha, DEFAULT_TIMEOUT_SECONDS);
        return Result.success("获取短信验证码成功.", smsCaptcha);
    }

    @GetMapping("/getCaptcha")
    public Result<CaptchaResult> getCaptcha() {
        // 字符串挑选模板
        String baseCharNumber = "abcdefghijklmnopqrstuvwxyz".toUpperCase() + "abcdefghijklmnopqrstuvwxyz0123456789";
        // 随机字符串长度
        int randomLength = 4;

        StringBuilder captchaBuilder = new StringBuilder(randomLength);

        for(int i = 0; i < randomLength; ++i) {
            int number = ThreadLocalRandom.current().nextInt(baseCharNumber.length());
            captchaBuilder.append(baseCharNumber.charAt(number));
        }

        String captcha = captchaBuilder.toString();
        // 生成一个唯一id
        long id = IdWorker.getId();
        // 存入缓存中，5分钟后过期
        redisOperator.set((IMAGE_CAPTCHA_PREFIX_KEY + id), captcha, DEFAULT_TIMEOUT_SECONDS);
        return Result.success("获取验证码成功.", new CaptchaResult(String.valueOf(id), captcha, (null)));
    }

}
