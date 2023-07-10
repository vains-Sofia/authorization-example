package com.example.authorization.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 重定向至登录处理
 *
 * @author vains
 */
public class LoginTargetAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public LoginTargetAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 获取登录表单的地址
        String loginForm = determineUrlToUseForThisRequest(request, response, authException);
        if (!UrlUtils.isAbsoluteUrl(loginForm)) {
            // 不是绝对路径调用父类方法处理
            super.commence(request, response, authException);
            return;
        }

        StringBuffer requestUrl = request.getRequestURL();
        if (!ObjectUtils.isEmpty(request.getQueryString())) {
            requestUrl.append("?").append(request.getQueryString());
        }

        // 绝对路径在重定向前添加target参数
        String targetUrl = URLEncoder.encode(requestUrl.toString(), StandardCharsets.UTF_8);
        this.redirectStrategy.sendRedirect(request, response, (loginForm + "?target=" + targetUrl));

    }
}
