package com.example.authorization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.io.IOException;

/**
 * OAuth 2.0 令牌的标准数据格式的自定义序列化器
 *
 * @author Yu jinxiang
 */
public class OAuth2TokenFormatSerializer extends JsonSerializer<OAuth2TokenFormat> {

    @Override
    public void serialize(OAuth2TokenFormat value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.getValue());
    }

    @Override
    public void serializeWithType(OAuth2TokenFormat value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(value.getValue());
    }
}
