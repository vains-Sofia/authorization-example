package com.example.config.natives;


import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.AuthorizationExampleApplication;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * lambda 表达式注入到graal中
 *
 * @author ztp
 * @since 2023/8/18 11:53
 */
public class LambdaRegistrationFeature implements Feature {

    /**
     * 找到某个包下面指定的父类的所有子类
     *
     * @param packageName 包名
     * @param superClass  父类
     * @return 子类集合
     */
    public static List<Class<?>> findClasses(String packageName, Class<?> superClass) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter filter = new AssignableTypeFilter(superClass);

        scanner.addIncludeFilter(filter);

        List<Class<?>> classes = new ArrayList<>();
        String basePackage = ClassUtils.convertClassNameToResourcePath(packageName);
        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(candidate.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                // 处理异常
                throw new RuntimeException(e);
            }
        }

        return classes;
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        // 扫描指定包下IService的字类（实现类），然后全部注册到graalvm Lambda 序列化中
        LambdaRegistrationFeature.findClasses("com.example", IService.class)
                .forEach(RuntimeSerialization::registerLambdaCapturingClass);
        // 这里需要将lambda表达式所使用的成员类都注册上来,具体情况视项目情况而定,一般扫描@Controller和@Service的会多点.
        RuntimeSerialization.registerLambdaCapturingClass(AuthorizationExampleApplication.class);
        RuntimeSerialization.register(SerializedLambda.class, SFunction.class);
    }

}