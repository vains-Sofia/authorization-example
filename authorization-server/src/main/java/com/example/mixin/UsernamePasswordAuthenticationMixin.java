package com.example.mixin;

import com.example.entity.Oauth2BasicUser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.boot.jackson.JsonMixin;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.IOException;

/**
 * 反序列化UsernamePasswordAuthenticationToken类
 * {@link JsonMixin } 代表给指定类提供一个Mixin实现类(这里是UsernamePasswordAuthenticationToken)，
 * 使用该注解时代表在 ObjectMapper中加一个映射，序列化和反序列化时使用该类进行操作
 * 这里就使用了 {@link JsonDeserialize} 注解指定反序列化逻辑为当前类
 *
 * @author vains
 */
@JsonMixin(UsernamePasswordAuthenticationToken.class)
@JsonDeserialize(using = UsernamePasswordAuthenticationMixin.class)
public class UsernamePasswordAuthenticationMixin extends JsonDeserializer<UsernamePasswordAuthenticationToken> {

    /**
     * 实现 UsernamePasswordAuthenticationToken 的反序列化逻辑
     *
     * @param jsonParser             Parsed used for reading JSON content
     * @param deserializationContext Context that can be used to access information about
     *                               this deserialization activity.
     * @return 序列化之后得到的 {@link UsernamePasswordAuthenticationToken}类的实例
     * @throws IOException      读取json失败时抛出
     * @throws JacksonException 将json转为java类失败时抛出
     */
    @Override
    public UsernamePasswordAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        // 获取序列化器
        ObjectCodec codec = jsonParser.getCodec();
        // 获取json树对象
        TreeNode treeNode = codec.readTree(jsonParser);
        // 获取json树中的认证信息
        TreeNode principal = treeNode.get("principal");
        // 将认证信息转为用户表中的信息类
        Oauth2BasicUser oauth2BasicUser = codec.treeToValue(principal, Oauth2BasicUser.class);
        // 根据认证信息初始化UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(oauth2BasicUser, (null), oauth2BasicUser.getAuthorities());
        // 获取web身份认证时的http信息
        TreeNode details = treeNode.get("details");
        // 转为 WebAuthenticationDetails
        WebAuthenticationDetails webAuthenticationDetails = codec.treeToValue(details, WebAuthenticationDetails.class);
        // 放入认证信息中
        authenticationToken.setDetails(webAuthenticationDetails);
        return authenticationToken;
    }

}
