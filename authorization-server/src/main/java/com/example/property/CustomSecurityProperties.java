package com.example.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.property.CustomSecurityProperties.PREFIX;

/**
 * 自定义认证配置类
 *
 * @author vains
 */
@Data
@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class CustomSecurityProperties {

    static final String PREFIX = "custom.security";

    /**
     * 登录页面地址
     * 注意：不是前后端分离的项目不要写完整路径，当前项目部署的IP也不行！！！
     * 错误e.g. http://当前项目IP:当前项目端口/activated
     */
    private String loginUrl = "/login";

    /**
     * 授权确认页面
     * 注意：不是前后端分离的项目不要写完整路径，当前项目部署的IP也不行！！！
     * 错误e.g. http://当前项目IP:当前项目端口/activated
     */
    private String consentPageUri = "/oauth2/consent";

    /**
     * 授权码验证页面
     * 注意：不是前后端分离的项目不要写完整路径，当前项目部署的IP也不行！！！
     * 错误e.g. http://当前项目IP:当前项目端口/activated
     */
    private String deviceActivateUri = "/activate";

    /**
     * 授权码验证成功后页面
     * 注意：不是前后端分离的项目不要写完整路径，当前项目部署的IP也不行！！！
     * 错误e.g. http://当前项目IP:当前项目端口/activated
     */
    private String deviceActivatedUri = "/activated";

    /**
     * 不需要认证的路径
     */
    private List<String> ignoreUriList;

    /**
     * 设置token签发地址(http(s)://{ip}:{port}/context-path, http(s)://domain.com/context-path)
     * 如果需要通过ip访问这里就是ip，如果是有域名映射就填域名，通过什么方式访问该服务这里就填什么
     */
    private String issuerUrl;

}
