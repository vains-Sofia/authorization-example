package com.example.config.natives;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(HintsRegistrationConfig.HintsRegistration.class)
public class HintsRegistrationConfig {

    static class HintsRegistration implements RuntimeHintsRegistrar {

        private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            ReflectionHints reflection = hints.reflection();
            try {
                bindingRegistrar.registerReflectionHints(reflection, Class.forName("org.springframework.security.oauth2.server.authorization.jackson2.UnmodifiableMapDeserializer"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
