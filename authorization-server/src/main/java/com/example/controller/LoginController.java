package com.example.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.model.Result;
import com.example.model.response.CaptchaResult;
import com.example.support.RedisOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        String smsCaptcha = "1234";
        // 存入缓存中，5分钟后过期
        redisOperator.set((SMS_CAPTCHA_PREFIX_KEY + phone), smsCaptcha, DEFAULT_TIMEOUT_SECONDS);
        return Result.success("获取短信验证码成功.", smsCaptcha);
    }

    @GetMapping("/getCaptcha")
    public Result<CaptchaResult> getCaptcha() {
        // 使用huTool-captcha生成图形验证码
        // 定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(130, 34, 4, 2);
        // 生成一个唯一id
        long id = IdWorker.getId();
        // 存入缓存中，5分钟后过期
        redisOperator.set((IMAGE_CAPTCHA_PREFIX_KEY + id), captcha.getCode(), DEFAULT_TIMEOUT_SECONDS);
        return Result.success("获取验证码成功.", new CaptchaResult(String.valueOf(id), captcha.getCode(), captcha.getImageBase64Data()));
    }

}
