package com.example.constant;

/**
 * Redis相关常量
 *
 * @author vains
 */
public class RedisConstants {

    /**
     * 短信验证码前缀
     */
    public static final String SMS_CAPTCHA_PREFIX_KEY = "mobile_phone:";

    /**
     * 图形验证码前缀
     */
    public static final String IMAGE_CAPTCHA_PREFIX_KEY = "image_captcha:";

    /**
     * 验证码过期时间，默认五分钟
     */
    public static final long CAPTCHA_TIMEOUT_SECONDS = 60L * 5;

}
