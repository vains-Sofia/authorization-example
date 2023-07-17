package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.constant.SecurityConstants;
import com.example.entity.Oauth2BasicUser;
import com.example.entity.Oauth2ThirdAccount;
import com.example.entity.SysAuthority;
import com.example.model.response.Oauth2UserinfoResult;
import com.example.model.security.CustomGrantedAuthority;
import com.example.service.IOauth2BasicUserService;
import com.example.service.IOauth2ThirdAccountService;
import com.example.service.ISysAuthorityService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证服务器相关自定接口
 *
 * @author vains
 */
@Controller
@RequiredArgsConstructor
public class AuthorizationController {

    private final ISysAuthorityService authorityService;

    private final IOauth2BasicUserService basicUserService;

    private final IOauth2ThirdAccountService thirdAccountService;

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationConsentService authorizationConsentService;

    @ResponseBody
    @GetMapping("/user")
    public Oauth2UserinfoResult user(Principal principal) {
        Oauth2UserinfoResult result = new Oauth2UserinfoResult();
        if (!(principal instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return result;
        }
        // 获取jwt解析内容
        Jwt token = jwtAuthenticationToken.getToken();
        // 获取当前用户的账号
        String account = token.getClaim(JwtClaimNames.SUB);
        // 获取scope
        List<String> scopes = token.getClaimAsStringList("scope");
        List<String> claimAsStringList = token.getClaimAsStringList(SecurityConstants.AUTHORITIES_KEY);
        if (!ObjectUtils.isEmpty(claimAsStringList)) {
            scopes = null;
        }
        LambdaQueryWrapper<Oauth2BasicUser> accountWrapper = Wrappers.lambdaQuery(Oauth2BasicUser.class)
                .eq(Oauth2BasicUser::getAccount, account);
        Oauth2BasicUser basicUser = basicUserService.getOne(accountWrapper);
        if (basicUser != null) {
            // 填充用户的权限信息
            this.fillUserAuthority(claimAsStringList, basicUser, scopes);
            BeanUtils.copyProperties(basicUser, result);
            // 根据用户信息查询三方登录信息
            LambdaQueryWrapper<Oauth2ThirdAccount> userIdWrapper =
                    Wrappers.lambdaQuery(Oauth2ThirdAccount.class)
                            .eq(Oauth2ThirdAccount::getUserId, basicUser.getId());
            Oauth2ThirdAccount oauth2ThirdAccount = thirdAccountService.getOne(userIdWrapper);
            if (oauth2ThirdAccount == null) {
                return result;
            }
            result.setCredentials(oauth2ThirdAccount.getCredentials());
            result.setThirdUsername(oauth2ThirdAccount.getThirdUsername());
            result.setCredentialsExpiresAt(oauth2ThirdAccount.getCredentialsExpiresAt());
            return result;
        }
        // 根据当前sub去三方登录表去查
        LambdaQueryWrapper<Oauth2ThirdAccount> wrapper = Wrappers.lambdaQuery(Oauth2ThirdAccount.class)
                .eq(Oauth2ThirdAccount::getUniqueId, account)
                .eq(Oauth2ThirdAccount::getType, token.getClaim("loginType"));
        Oauth2ThirdAccount oauth2ThirdAccount = thirdAccountService.getOne(wrapper);
        if (oauth2ThirdAccount == null) {
            return result;
        }
        // 查到之后反查基础用户表
        Oauth2BasicUser oauth2BasicUser = basicUserService.getById(oauth2ThirdAccount.getUserId());
        BeanUtils.copyProperties(oauth2BasicUser, result);
        // 填充用户的权限信息
        this.fillUserAuthority(claimAsStringList, oauth2BasicUser, scopes);
        // 复制基础用户信息
        BeanUtils.copyProperties(oauth2BasicUser, result);
        // 设置三方用户信息
        result.setCredentials(oauth2ThirdAccount.getCredentials());
        result.setThirdUsername(oauth2ThirdAccount.getThirdUsername());
        result.setCredentialsExpiresAt(oauth2ThirdAccount.getCredentialsExpiresAt());
        return result;
    }

    private void fillUserAuthority(List<String> claimAsStringList, Oauth2BasicUser basicUser, List<String> scopes) {
        if (ObjectUtils.isEmpty(claimAsStringList)) {
            // 如果获取不到权限信息去数据库查
            List<SysAuthority> sysAuthorities = authorityService.getByUserId(basicUser.getId());
            Set<CustomGrantedAuthority> authorities = sysAuthorities.stream()
                    .map(SysAuthority::getAuthority)
                    .map(CustomGrantedAuthority::new)
                    .collect(Collectors.toSet());
            if (!ObjectUtils.isEmpty(scopes)) {
                scopes.stream().map(CustomGrantedAuthority::new).forEach(authorities::add);
            }
            basicUser.setAuthorities(authorities);
        } else {
            Set<CustomGrantedAuthority> authorities = claimAsStringList.stream()
                    .map(CustomGrantedAuthority::new)
                    .collect(Collectors.toSet());
            // 否则设置为token中获取的
            basicUser.setAuthorities(authorities);
        }
    }

    @GetMapping("/activate")
    public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
        if (userCode != null) {
            return "redirect:/oauth2/device_verification?user_code=" + userCode;
        }
        return "device-activate";
    }

    @GetMapping("/activated")
    public String activated() {
        return "device-activated";
    }

    @GetMapping(value = "/", params = "success")
    public String success() {
        return "device-activated";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        Object attribute = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (attribute instanceof AuthenticationException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        return "login";
    }

    @GetMapping(value = "/oauth2/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                          @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

        // Remove scopes that were already approved
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new RuntimeException("客户端不存在");
        }
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", withDescription(scopesToApprove));
        model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("userCode", userCode);
        if (StringUtils.hasText(userCode)) {
            model.addAttribute("requestURI", "/oauth2/device_verification");
        } else {
            model.addAttribute("requestURI", "/oauth2/authorize");
        }

        return "consent";
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));

        }
        return scopeWithDescriptions;
    }

    @Data
    public static class ScopeWithDescription {
        private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(
                    OidcScopes.PROFILE,
                    "This application will be able to read your profile information."
            );
            scopeDescriptions.put(
                    "message.read",
                    "This application will be able to read your message."
            );
            scopeDescriptions.put(
                    "message.write",
                    "This application will be able to add new messages. It will also be able to edit and delete existing messages."
            );
            scopeDescriptions.put(
                    "other.scope",
                    "This is another scope example of a scope description."
            );
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
        }
    }

}