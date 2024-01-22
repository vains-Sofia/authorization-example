## 使用spring session data redis实现的授权码模式前后端分离

**无需借助nonceId保持认证状态，前端后端相关内容已经全部去除**

### 有以下必要条件
- 认证服务与前端服务在同一顶级域名下的不同子域名
> 例如：认证服务是auth.a.com，前端是page.a.com，他们都是顶级域名a.com下
- 引入 spring session data redis 依赖
- 在yml中设置cookie的domain配置
```yml
server:
  servlet:
    session:
      cookie:
        # 这里设置顶级域名
        domain: flow.com 
```

### 本地调试配置
1. 配置hosts文件，在hosts文件中添加两个dns映射
```
# 随便设置的域名
127.0.0.1       auth.flow.com
127.0.0.1       login.flow.com
```
2. 配置nginx，添加两个域名的映射

也可以不用nginx，将上边所说的yml中的domain配置为127.0.0.1，访问时使用127.0.0.1访问前端和认证服务。
```
server {
    listen       80;
    server_name  login.flow.com auth.flow.com;

    #charset koi8-r;

    #access_log  logs/host.access.log  main;
    
    # 前端登录页代理
    location /page {
        proxy_pass http://127.0.0.1:5173/page;
    }

    # 认证服务代理
    location / {
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_pass http://127.0.0.1:7000;
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   html;
    }

}
```

**线上直接将不同二级域名解析到对应服务就行，根据自己业务调整**

**这里是将认证服务和前端服务添加在同一个server中了，只根据location匹配服务，server_name同时设置了两个域名，两个域名都可以访问到这里，所以在配置时(yml配置和.env.[环境]文件中的配置)要区分好两个服务的域名**

设置完成以后就可以进行本地调试了