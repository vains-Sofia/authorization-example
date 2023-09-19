package com.example.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 基础OAuth2用户信息类
 *
 * @author vains
 */
@Data
@JsonSerialize
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicOAuth2User implements OAuth2User, Serializable {

    /**
     * 自增id
     */
    private Integer id;

    /**
     * 三方登录唯一id
     */
    private String uniqueId;

    /**
     * 三方登录用户名
     */
    private String thirdUsername;

    /**
     * 三方登录获取的认证信息
     */
    private String credentials;

    /**
     * 三方登录获取的认证信息的过期时间
     */
    private LocalDateTime credentialsExpiresAt;

    /**
     * 三方登录类型
     */
    private String type;

    /**
     * 博客地址
     */
    private String blog;

    /**
     * 网格地址
     */
    private String location;


    /**
     * 用户表主键
     */
    private Integer userId;

    /**
     * 绑定时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户名、昵称
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 头像地址
     */
    private String avatarUrl;
    
    /**
     * 用户来源
     */
    private String sourceFrom;


    /**
     * 三方账号字段名
     */
    private String nameAttributeKey;

    /**
     * 三方用户属性信息
     */
    private Map<String, Object> attributes;

    /**
     * 三方用户权限信息(scope)
     */
    private Set<GrantedAuthority> authorities;

    @Override
    public String getName() {
        return attributes.get(nameAttributeKey) + "";
    }

    public BasicOAuth2User(OAuth2User oAuth2User, String nameAttributeKey) {
        Assert.notEmpty(oAuth2User.getAttributes(), "attributes cannot be empty");
        this.authorities = (oAuth2User.getAttributes() != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(oAuth2User.getAuthorities())))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(oAuth2User.getAttributes()));
        this.nameAttributeKey = nameAttributeKey;
    }

    /**
     * 将权限排序
     *
     * @param authorities 权限
     * @return 排序后的权限信息
     */
    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }

}