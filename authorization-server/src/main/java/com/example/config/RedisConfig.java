package com.example.config;

import com.example.util.JsonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis的key序列化配置类
 *
 * @author vains
 */
@Configuration
public class RedisConfig {

    /**
     * 默认情况下使用
     *
     * @param connectionFactory redis链接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 存入redis时序列化值的序列化器
        Jackson2JsonRedisSerializer<Object> valueSerializer =
                new Jackson2JsonRedisSerializer<>(JsonUtils.MAPPER, Object.class);

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        // 设置hash格式数据值的序列化器
        redisTemplate.setHashValueSerializer(valueSerializer);
        // 默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置字符串序列化器
        redisTemplate.setStringSerializer(stringRedisSerializer);
        // 设置hash结构的key的序列化器
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 设置连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

    /**
     * 操作hash的情况下使用
     *
     * @param connectionFactory redis链接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisHashTemplate(RedisConnectionFactory connectionFactory) {

        return redisTemplate(connectionFactory);
    }

}