package com.example.config.natives;

import com.example.controller.AuthorizationController;
import org.springframework.aot.hint.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.thymeleaf.expression.Lists;

import java.util.Arrays;

/**
 * 认证服务 整合Spring Data Redis后自定义核心services保存客户端、认证、授权确认信息至Redis打包本机镜像配置类
 *
 * @author vains
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(AuthorizationServerRegistrationConfiguration.HintsRegistration.class)
public class AuthorizationServerRegistrationConfiguration {

    static class HintsRegistration implements RuntimeHintsRegistrar {

        private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            ReflectionHints reflection = hints.reflection();
            try {
                // Thymeleaf
                hints.reflection().registerTypes(
                        Arrays.asList(
                                TypeReference.of(Lists.class),
                                TypeReference.of(AuthorizationGrantType.class),
                                TypeReference.of(AuthorizationController.ScopeWithDescription.class)
                        ), builder ->
                                builder.withMembers(MemberCategory.DECLARED_FIELDS,
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS)
                );

                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.jackson2.UnmodifiableMapDeserializer"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.jackson2.UnmodifiableSetDeserializer"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.jackson2.UnmodifiableListDeserializer"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.web.jackson2.WebAuthenticationDetailsMixin"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.jackson2.UsernamePasswordAuthenticationTokenMixin"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.jackson2.UsernamePasswordAuthenticationTokenDeserializer"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.oauth2.server.authorization.jackson2.OAuth2TokenFormatMixin"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.oauth2.server.authorization.jackson2.UnmodifiableMapDeserializer"));
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationRequestDeserializer"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
