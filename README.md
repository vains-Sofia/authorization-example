# Spring oauth2 authorization server示例项目

## 项目说明

Spring OAuth2 Authorization Server集成与拓展项目，包括认证服务搭建、三方登录对接、自定义grant_type方式获取token、前后端分离实现，客户端、资源服务、前端单体项目对接认证服务实现等。

## 项目Wiki地址(持续更新)

[Wiki](https://gitee.com/vains-Sofia/authorization-example/wikis/pages)

## 登录流程演示地址
[演示地址](http://k7fsqkhtbx.cdhttp.cn)

## 交流群
[724141959](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=TJ6P1zOGofljK4xxnRrmn_3p42V995OD&authKey=h3YWSUIJXL23m34ATDfWlYa61UQRyBRxBpoMnDGoU%2BJKKfcHWGYMZC9bAJ%2FZ69Ax&noverify=0&group_code=724141959)

## 项目支持的授权方式

授权码模式

客户端模式

授权码扩展流程PKCE(Proof Key for Code Exchange)

短信登录(自定义grant_type)

设备码授权模式

## 项目支持的登录方式
短信验证码登录

授权码模式使用前后端分离的登录页面(现已支持spring session data redis，前后端不同域名的可以看看之前的nonceId的方案)

联合身份认证(现已集成Github、Gitee登录和微信登录)

二维码扫码登录

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
 ├─vue-login-page-example # 前后端登录中的前端模块
 │  
 ├─both-jwt-and-opaque-token-resource-example # 兼容jwt和不透明令牌(opaque token)资源服务示例
 │  
 ├─opaque-token-resource-example # 不透明令牌(opaque token)资源服务示例
 │  
 └─gateway-example # 网关集成OAuth2认证服务示例
     │  
     ├─gateway-client-example # 网关
     │  
     ├─normal-resource-example # webmvc资源服务
     │  
     ├─webflux-resource-example # webflux资源服务
     │  
     └─pom.xml # 公共依赖，依赖管理
```

## 文章地址

### [掘金专栏](https://juejin.cn/column/7239953874950684732)

### [CSDN专栏](https://blog.csdn.net/weixin_43356507/category_12338180.html)

## 赞赏

**如果各位觉得项目或文章还不错的，麻烦帮忙点个star，谢谢**<br />
**也可以通过下方二维码进行赞赏，请作者喝一杯Coffee，非常感谢！**
![Reward](images/Reward.jpg)


## 感谢人列表

| 赞助人昵称             | 金额    | 赞助方式 | 附言                          |
|-------------------|-------| -------- |-----------------------------|
| 寒雨潇何              | 10    | 赞赏码   | 感谢老哥的无私付出                   |
| 寒雨潇何              | 10    | 赞赏码   | 再次感谢老哥的指导                   |
| 唐亚峰 &#124; battcn | 88    | 微信红包   | 喝杯咖啡                        |
| HWF               | 68    | 赞赏码   | 非常好的项目 :clap: :clap: :clap: |
| 恒辉                | 12.88 | 微信红包   | 非常感谢大佬，辛苦啦                  |
| Eric              | 10    | 赞赏码   | 感谢大佬开源项目，学到很多。              |
| Eric              | 10    | 赞赏码   | 感谢大佬指导                      |
| WestonLee         | 18.88 | 赞赏码   | ctrl+c ctrl+v 上线            |
| 贱贱                | 20    | 赞赏码   | 搞个交流群!!!                    |
| Eric              | 10    | 赞赏码   | 大佬 国庆快乐 :+1:                |
| outas             | 66    | 赞赏码  | 感谢指导                        |
| 宫城良田              | 18.88 | 赞赏码  | 感谢大佬指导                      |
| mango             | 20    | 赞赏码  | nice work                   |
| \*\*\*(匿名)        | 100   | 微信红包  | 写的太好啦 哥们继续努力                |
| Q                 | 28.88 | 赞赏码  | 感谢大佬的指导                     |
| 小增                | 5     | 赞赏码  | c v即将上线，喝个快乐水               |
| Q                 | 10    | 赞赏码  | 感谢指导，文章非常好                  |
| alpha             | 66    | 赞赏码  | 感谢大佬开源项目，感谢指导               |
| 文                 | 20    | 赞赏码  | 感谢大佬，奉献如此好的项目               |
| Q                 | 10    | 微信红包  | -                           |
| 向阳                | 30    | 赞赏码  | 大佬天冷了喝杯恰啡                   |
| HWF               | 18    | 赞赏码  | 喝杯奶茶，暖暖身子                   |
| gn                | 20    | 赞赏码  | 大佬文档写的好，出了问题还无私             |
| gn                | 20    | 赞赏码  | 刚才少了，在赞助下                   |
| gn                | 20    | 赞赏码  | 感谢支持                        |
| 端碗吹水              | 20    | 赞赏码  | 感谢老哥的辛勤付出                   |
| FishLin           | 88    | 微信红包  | 新年快乐                        | 
| 安一老厨              | 16.8  | 微信红包  | 安一兰心                        | 


**非常感谢以上小伙伴的打赏！感谢**

