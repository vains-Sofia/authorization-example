package com.example;

import com.example.entity.Oauth2BasicUser;
import com.example.support.RedisOperator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类测试
 *
 * @author vains
 */
@Slf4j
@SpringBootTest
public class RedisOperatorTests {

    @Autowired
    private RedisOperator<String> redisOperator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisOperator<Oauth2BasicUser> userRedisOperator;

    @Test
    @SneakyThrows
    void contextLoads() {
        // 默认key
        String defaultKey = "testKey";
        // 默认缓存值
        String defaultValue = "123456";
        // key的存活时间
        long timeout = 3;
        // 操作hash的属性声明
        String name = "name";

        // 清除key
        redisOperator.delete(defaultKey);

        // 获取用户信息
        Oauth2BasicUser userDetails = (Oauth2BasicUser) userDetailsService.loadUserByUsername("admin");

        redisOperator.set(defaultKey, defaultValue);
        log.info("根据key：{}存入值{}", defaultKey, defaultValue);

        String valueByKey = redisOperator.get(defaultKey);
        log.info("根据key：{}获取到值：{}", defaultKey, valueByKey);

        String valueByKeyAndDelete = redisOperator.getAndDelete(defaultKey);
        log.info("根据key：{}获取到值：{},删除key.", defaultKey, valueByKeyAndDelete);

        Long delete = redisOperator.delete(defaultKey);
        log.info("删除key：{}，删除数量：{}.", defaultKey, delete);

        valueByKey = redisOperator.get(defaultKey);
        log.info("根据key：{}获取到值：{}", defaultKey, valueByKey);

        redisOperator.set(defaultKey, defaultValue, timeout);
        log.info("根据key：{}存入值{}，存活时长为：{}", defaultKey, defaultValue, timeout);
        valueByKey = redisOperator.get(defaultKey);
        log.info("根据key：{}获取到值：{}", defaultKey, valueByKey);

        // 睡眠，让key失效
        TimeUnit.SECONDS.sleep((timeout + 1));

        // 重复获取
        valueByKey = redisOperator.get(defaultKey);
        log.info("线程睡眠后根据失效的key：{}获取到值：{}", defaultKey, valueByKey);

        redisOperator.setHashAll(defaultKey, userDetails, timeout);
        log.info("根据key：{}存入hash类型值{},存活时间：{}", defaultKey, userDetails, timeout);

        Oauth2BasicUser basicUser = redisOperator.getHashAll(defaultKey, Oauth2BasicUser.class);
        log.info("根据key：{}获取到hash类型值：{}", defaultKey, basicUser);

        // 睡眠，让key失效
        TimeUnit.SECONDS.sleep((timeout + 1));
        // 重复获取
        basicUser = redisOperator.getHashAll(defaultKey, Oauth2BasicUser.class);
        log.info("线程睡眠后根据失效的key：{}获取到hash类型值：{}", defaultKey, basicUser);

        redisOperator.setHashAll(defaultKey, userDetails, timeout);
        log.info("根据key：{}存入hash类型值{},存活时间：{}", defaultKey, userDetails, timeout);

        Map<String, Object> mapHashAll = redisOperator.getMapHashAll(defaultKey);
        log.info("根据key：{}获取到hash类型值：{}", defaultKey, mapHashAll);

        Object field = redisOperator.getHash(defaultKey, name);
        log.info("根据key：{}获取到hash类型属性：{}的值：{}", defaultKey, name, field);

        Long deleteHashField = redisOperator.deleteHashField(defaultKey, name);
        log.info("根据key：{}删除hash类型的{}属性，删除数量：{}", defaultKey, name, deleteHashField);

        // 重复获取验证删除
        field = redisOperator.getHash(defaultKey, name);
        log.info("根据key：{}获取到hash类型属性：{}的值：{}", defaultKey, name, field);
        basicUser = redisOperator.getHashAll(defaultKey, Oauth2BasicUser.class);
        log.info("根据key：{}获取到hash类型值：{}", defaultKey, basicUser);

        redisOperator.setHash(defaultKey, name, userDetails.getName());
        log.info("根据key：{}设置hash类型的{}属性，属性值为：{}", defaultKey, name, userDetails.getName());

        // 重复获取验证删除
        field = redisOperator.getHash(defaultKey, name);
        log.info("根据key：{}获取到hash类型属性：{}的值：{}", defaultKey, name, field);
        basicUser = redisOperator.getHashAll(defaultKey, Oauth2BasicUser.class);
        log.info("根据key：{}获取到hash类型值：{}", defaultKey, basicUser);

        // 清除key
        redisOperator.delete(defaultKey);

        userRedisOperator.listPush(defaultKey, userDetails);
        log.info("根据key：{}往list类型数据中添加数据：{}", defaultKey, userDetails);

        Collection<Oauth2BasicUser> users = userRedisOperator.getList(defaultKey);
        log.info("根据key：{}获取list数据：{}", defaultKey, users);

        Long listPushAll = userRedisOperator.listPushAll(defaultKey, List.of(userDetails));
        log.info("根据key：{}往list类型数据中添加数据：{}，成功添加{}条数据", defaultKey, List.of(userDetails), listPushAll);

        users = userRedisOperator.getList(defaultKey);
        log.info("根据key：{}获取list数据：{}", defaultKey, users);

        userRedisOperator.listPush(defaultKey, userDetails, timeout);
        log.info("根据key：{}往list类型数据中添加数据：{}，key的存活时间为：{}", defaultKey, userDetails, timeout);
        // 睡眠，让key失效
        TimeUnit.SECONDS.sleep((timeout + 1));
        // 重复获取
        users = userRedisOperator.getList(defaultKey);
        log.info("线程睡眠后根据失效的key：{}获取到list类型值：{}", defaultKey, users);

        Long aLong = userRedisOperator.listPushAll(defaultKey, List.of(userDetails), timeout);
        log.info("根据key：{}往list类型数据中添加数据：{}，成功添加{}条数据，设置过期时间：{}", defaultKey, List.of(userDetails), aLong, timeout);
    }

}
