import Cookies from "js-cookie";
import { storageLocal } from "@pureadmin/utils";
import { useUserStoreHook } from "@/store/modules/user";
import CryptoJS from 'crypto-js'
import router from "@/router";
import { getTopMenu, initRouter } from "@/router/utils";

export interface DataInfo<T> {
  /** token */
  accessToken: string;
  /** `accessToken`的过期时间（时间戳） */
  expires: T;
  /** 用于调用刷新accessToken的接口时所需的token */
  refreshToken: string;
  /** accessToken类型 */
  tokenType: string;
  /** id token */
  idToken?: string;
  /** 授权申请的权限 */
  scopes?: Array<string>;
  /** 用户名 */
  username?: string;
  /** 当前登陆用户的角色 */
  roles?: Array<string>;
}

export interface OAuthClientInfo {
  /**
   * 认证授权服务地址
   */
  authorizeUri: string;
  /**
   * 客户端id
   */
  clientId: string;
  /**
   * 客户端秘钥
   */
  clientSecret?: string;
  /**
   * 回调地址
   */
  redirectUri: string;
  /**
   * 授权申请的权限
   */
  scopes: Array<string>;
  /**
   * 授权类型
   */
  grantType?: string;
}

export const userKey = "user-info";
export const stateKey = "state";
export const TokenKey = "authorized-token";
export const beforeLoginUriKey = "before-login-uri";
/**
 * 通过`multiple-tabs`是否在`cookie`中，判断用户是否已经登录系统，
 * 从而支持多标签页打开已经登录的系统后无需再登录。
 * 浏览器完全关闭后`multiple-tabs`将自动从`cookie`中销毁，
 * 再次打开浏览器需要重新登录系统
 * */
export const multipleTabsKey = "multiple-tabs";

/** 获取`token` */
export function getToken(): DataInfo<number> {
  // 此处与`TokenKey`相同，此写法解决初始化时`Cookies`中不存在`TokenKey`报错
  return Cookies.get(TokenKey)
    ? JSON.parse(Cookies.get(TokenKey))
    : storageLocal().getItem(userKey);
}

/**
 * @description 设置`token`以及一些必要信息并采用无感刷新`token`方案
 * 无感刷新：后端返回`accessToken`（访问接口使用的`token`）、`refreshToken`（用于调用刷新`accessToken`的接口时所需的`token`，`refreshToken`的过期时间（比如30天）应大于`accessToken`的过期时间（比如2小时））、`expires`（`accessToken`的过期时间）
 * 将`accessToken`、`expires`这两条信息放在key值为authorized-token的cookie里（过期自动销毁）
 * 将`username`、`roles`、`refreshToken`、`expires`这四条信息放在key值为`user-info`的localStorage里（利用`multipleTabsKey`当浏览器完全关闭后自动销毁）
 */
export function setToken(data: DataInfo<number>) {
  let expires = 0;
  const { accessToken, refreshToken, tokenType, scopes } = data;
  const { isRemembered, loginDay } = useUserStoreHook();
  expires = data.expires; // 如果后端直接设置时间戳，将此处代码改为expires = data.expires，然后把上面的DataInfo<Date>改成DataInfo<number>即可
  const cookieString = JSON.stringify({ accessToken, expires, tokenType, scopes });

  expires > 0
    ? Cookies.set(TokenKey, cookieString, {
      expires: new Date(expires)
    })
    : Cookies.set(TokenKey, cookieString);

  Cookies.set(
    multipleTabsKey,
    "true",
    isRemembered
      ? {
        expires: loginDay
      }
      : {}
  );

  function setUserKey(username: string, roles: Array<string>) {
    useUserStoreHook().SET_USERNAME(username);
    useUserStoreHook().SET_ROLES(roles);
    storageLocal().setItem(userKey, {
      refreshToken,
      expires,
      username,
      roles
    });
  }

  if (data.username && data.roles) {
    const { username, roles } = data;
    setUserKey(username, roles);
  } else {
    const username =
      storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "";
    const roles =
      storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [];
    setUserKey(username, roles);
  }
}

/** 删除`token`以及key值为`user-info`的localStorage信息 */
export function removeToken() {
  Cookies.remove(TokenKey);
  Cookies.remove(multipleTabsKey);
  storageLocal().removeItem(userKey);
}

/** 格式化token（jwt格式） */
export const formatToken = (token: string): string => {
  return `bearer ${token}`;
};

/**
 * 生成授权申请url
 * @param oAuth2Client 客户端信息
 * @returns 返回授权申请url
 */
export const buildAuthorizeUri = (oAuth2Client: OAuthClientInfo): string => {
  const state = generateCodeVerifier();
  storageLocal().setItem(stateKey, state)
  return `${oAuth2Client.authorizeUri}?client_id=${oAuth2Client.clientId
    }&response_type=code&scope=${encodeURIComponent(oAuth2Client.scopes.join(' '))}&redirect_uri=${encodeURIComponent(oAuth2Client.redirectUri)
    }&state=${state}`
}

/**
 * 生成 CodeVerifier
 *
 * @returns CodeVerifier
 */
export const generateCodeVerifier = (): string => {
  return generateRandomString(32)
}

/**
 * 生成随机字符串
 * @param length 随机字符串的长度
 * @returns 随机字符串
 */
export const generateRandomString = (length: number): string => {
  let text = ''
  const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  for (let i = 0; i < length; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length))
  }
  return text
}

/**
 * 生成 Code Challenge
 * @param code_verifier 上边生成的 CodeVerifier
 * @returns Code Challenge
 */
export const generateCodeChallenge = (code_verifier: string): string => {
  return base64URL(CryptoJS.SHA256(code_verifier))
}

/**
 * 将字符串base64加密后在转为url string
 * @param str 字符串
 * @returns bese64转码后转为url string
 */
export const base64URL = (str: CryptoJS.lib.WordArray): string => {
  return str
    .toString(CryptoJS.enc.Base64)
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
}

/**
 * 将字符串加密为Base64格式的
 * @param str 将要转为base64的字符串
 * @returns 返回base64格式的字符串
 */
export const base64Str = (str: string): string => {
  return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(str));
}

/**
 * 根据参数name获取地址栏的参数
 * @param name 地址栏参数的key
 * @returns key对用的值
 */
export const getQueryString = (name: string): string => {
  const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')

  const r = window.location.search.substr(1).match(reg)

  if (r != null) {
    return decodeURIComponent(r[2])
  }

  return null
}

/**
 * 跳转到登录之前地址
 */
export const redirectToBeforeLoginUri = () => {
  const beforeLoginUri = storageLocal().getItem(beforeLoginUriKey)
  initRouter().then(() => router.push(beforeLoginUri ? beforeLoginUri : getTopMenu(true).path))
  storageLocal().removeItem(beforeLoginUriKey);
}