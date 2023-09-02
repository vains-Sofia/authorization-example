package com.example.task;

import com.example.exchange.ProjectExchange;
import com.example.model.Result;
import com.example.model.response.CaptchaResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 测试Http Interface，调用当前项目
 *
 * @author vains
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartBeatTask {

    private final ProjectExchange projectExchange;

    /**
     * 每25分钟请求一下当前服务
     */
    @Scheduled(fixedDelay = 28L, timeUnit = TimeUnit.MINUTES)
    public void keepServerLive() {
        try {
            log.info("开始调用当前项目接口...");

            Result<CaptchaResult> result = projectExchange.getCaptcha();

            log.info("调用当前服务成功，返回：{}", result.getData().getCode());
        } catch (Exception e) {
            log.error("调用当前服务失败，原因：{}", e.getMessage());
        }
    }

}