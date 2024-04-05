package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.awt.*;

/**
 * 反射测试类
 *
 * @author vains
 */
public class ReflectionTests {

    /**
     * 获取类中所有字段并按照jni配置规则生成json
     */
    @Test
    public void getDeclaredFields() {
        ReflectionUtils.doWithFields(Font.class, field -> {
            System.out.print(STR."{\"name\": \"\{field.getName()}\"}");
            System.out.println(",");
        });
    }

}
