# Spring oauth2 authorization server示例项目

## 项目说明

本项目如标题所说，是Spring oauth2 authorization server的一个整合项目。

## 项目支持的授权方式

授权码模式

客户端模式

授权码扩展流程PKCE(Proof Key for Code Exchange)

设备码授权模式

短信验证码登录

自定义grant_type

授权码模式使用前后端分离的登录页面

联合身份认证(现已集成Github、Gitee登录和微信登录)

## 项目环境要求

**Java版本大于等于17**

**Springboot版本大于等于3.1.0-RC1**

**IDE安装Lombok插件**

## 仓库内项目结构

```
authorization-example # 最外层目录
 │  README.md # 项目描述文件
 │  
 ├─authorization-server # 认证服务器
 │  
 ├─authorization-client-example # 客户端集成示例
 │  
 ├─resource-server-example # Resource Server集成示例
 │  
 └─vue-login-page-example # 前后端登录中的前端模块
```

## 文章地址

### 掘金

**[Spring Authorization Server入门 (一) 初识SpringAuthorizationServer和OAuth2.1协议](https://juejin.cn/post/7239953874950733884)**<br>
**[Spring Authorization Server入门 (二) springboot整合Spring Authorization Server](https://juejin.cn/post/7239953874950815804)**<br>
**[Spring Authorization Server入门 (三) 集成流程说明、细节补充和各种方式获取token测试](https://juejin.cn/post/7241058098974720037)**<br>
**[Spring Authorization Server入门 (四) 自定义设备码授权](https://juejin.cn/post/7241101553712283707)**<br>
**[Spring Authorization Server入门 (五) 自定义异常响应配置](https://juejin.cn/post/7241439405970063416)**<br>
**[Spring Authorization Server入门 (六) 自定义JWT中携带的claims与资源服务jwt解析器](https://juejin.cn/post/7241762957570097213)**<br>
**[Spring Authorization Server入门 (七) 登录添加图形验证码](https://juejin.cn/post/7242476048005709879)**<br>
**[Spring Authorization Server入门 (八) Spring Boot引入Security OAuth2 Client对接认证服务](https://juejin.cn/spost/7243725197911834683)**<br>
**[Spring Authorization Server入门 (九) Spring Boot引入Resource Server对接认证服务](https://juejin.cn/spost/7244043482772029498)**<br>
**[Spring Authorization Server入门 (十) 添加短信验证码方式登录](https://juejin.cn/post/7245538214114492474)**<br>
**[Spring Authorization Server入门 (十一) 自定义grant_type(短信认证登录)获取token](https://juejin.cn/post/7246409673565372475)**<br>
**[Spring Authorization Server入门 (十二) 实现授权码模式使用前后端分离的登录页面](https://juejin.cn/post/7254096495184134181)**<br>
**[Spring Authorization Server入门 (十三) 实现联合身份认证，集成Github与Gitee的OAuth登录](https://juejin.cn/post/7258466145653096504)**<br>
**[Spring Authorization Server入门 (十四) 联合身份认证添加微信登录](https://juejin.cn/post/7261098261142208568)**<br>

### CSDN

**[Spring Authorization Server入门 (一) 初识SpringAuthorizationServer和OAuth2.1协议](https://blog.csdn.net/weixin_43356507/article/details/130991406)**<br>
**[Spring Authorization Server入门 (二) springboot整合Spring Authorization Server](https://blog.csdn.net/weixin_43356507/article/details/131006763)**<br>
**[Spring Authorization Server入门 (三) 集成流程说明、细节补充和各种方式获取token测试](https://blog.csdn.net/weixin_43356507/article/details/131023147)**<br>
**[Spring Authorization Server入门 (四) 自定义设备码授权](https://blog.csdn.net/weixin_43356507/article/details/131050408)**<br>
**[Spring Authorization Server入门 (五) 自定义异常响应配置](https://blog.csdn.net/weixin_43356507/article/details/131063392)**<br>
**[Spring Authorization Server入门 (六) 自定义JWT中携带的claims与资源服务jwt解析器](https://blog.csdn.net/weixin_43356507/article/details/131081862)**<br>
**[Spring Authorization Server入门 (七) 登录添加图形验证码](https://blog.csdn.net/weixin_43356507/article/details/131109818)**<br>
**[Spring Authorization Server入门 (八) Spring Boot引入Security OAuth2 Client对接认证服务](https://blog.csdn.net/weixin_43356507/article/details/131173945)**<br>
**[Spring Authorization Server入门 (九) Spring Boot引入Resource Server对接认证服务](https://blog.csdn.net/weixin_43356507/article/details/131190785)**<br>
**[Spring Authorization Server入门 (十) 添加短信验证码方式登录](https://blog.csdn.net/weixin_43356507/article/details/131262461)**<br>
**[Spring Authorization Server入门 (十一) 自定义grant_type(短信认证登录)获取token](https://blog.csdn.net/weixin_43356507/article/details/131297456)**<br>
**[Spring Authorization Server入门 (十二) 实现授权码模式使用前后端分离的登录页面](https://blog.csdn.net/weixin_43356507/article/details/131650660)**<br>
**[Spring Authorization Server入门 (十三) 实现联合身份认证，集成Github与Gitee的OAuth登录](https://blog.csdn.net/weixin_43356507/article/details/131872353)**<br>
**[Spring Authorization Server入门 (十四) 联合身份认证添加微信登录](https://blog.csdn.net/weixin_43356507/article/details/131998050)**<br>