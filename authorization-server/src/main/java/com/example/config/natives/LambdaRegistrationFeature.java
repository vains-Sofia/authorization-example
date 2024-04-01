package com.example.config.natives;


import com.example.AuthorizationExampleApplication;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

/**
 * lambda 表达式注入到graal中
 * @author ztp
 * @since 2023/8/18 11:53
 */
public class LambdaRegistrationFeature implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        // 这里需要将lambda表达式所使用的成员类都注册上来,具体情况视项目情况而定,一般扫描@Controller和@Service的会多点.
        RuntimeSerialization.registerLambdaCapturingClass(AuthorizationExampleApplication.class);
    }

}