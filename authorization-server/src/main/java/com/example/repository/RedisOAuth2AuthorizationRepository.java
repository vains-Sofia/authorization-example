package com.example.repository;

import com.example.entity.security.RedisOAuth2Authorization;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * oauth2授权管理
 *
 * @author vains
 */
public interface RedisOAuth2AuthorizationRepository extends CrudRepository<RedisOAuth2Authorization, String> {

    /**
     * 根据授权码获取认证信息
     *
     * @param token 授权码
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByAuthorizationCodeValue(String token);

    /**
     * 根据access token获取认证信息
     *
     * @param token access token
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByAccessTokenValue(String token);

    /**
     * 根据刷新token获取认证信息
     *
     * @param token 刷新token
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByRefreshTokenValue(String token);

    /**
     * 根据id token获取认证信息
     *
     * @param token id token
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByOidcIdTokenValue(String token);

    /**
     * 根据用户码获取认证信息
     *
     * @param token 用户码
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByUserCodeValue(String token);

    /**
     * 根据设备码获取认证信息
     *
     * @param token 设备码
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByDeviceCodeValue(String token);

    /**
     * 根据state获取认证信息
     *
     * @param token 授权申请时的state
     * @return 认证信息
     */
    Optional<RedisOAuth2Authorization> findByState(String token);
}