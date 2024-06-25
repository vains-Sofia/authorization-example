import { http } from "@/utils/http";
import { OAuthClientInfo, base64Str } from '@/utils/auth'

export type UserResult = {
  success: boolean;
  data: {
    /** 用户名 */
    username: string;
    /** 当前登陆用户的角色 */
    roles: Array<string>;
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  };
};

export type RefreshTokenResult = {
  success: boolean;
  data: {
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  };
};

/**
 * 生成并获取二维码id响应类型
 */
export type GenerateQrCodeResult = {
  qrCodeId: string
}

/**
 * 生成并获取二维码id响应类型
 */
export type QrCodeInfo = {
  /**
     * 二维码状态
     * 0:待扫描，1:已扫描，2:已确认
     */
  qrCodeStatus?: number;

  /**
   * 是否已过期
   */
  expired?: boolean;

  /**
   * 扫描人头像
   */
  avatarUrl?: string;

  /**
   * 扫描人昵称
   */
  name?: string;

  /**
   * 待确认scope
   */
  scopes?: Array<string>;

  /**
   * 跳转登录之前请求的接口
   */
  beforeLoginRequestUri?: string;

  /**
   * 跳转登录之前请求参数
   */
  beforeLoginQueryString?: string;
}

/** 登录 */
export const getLogin = (data?: object) => {
  return http.post('/login', {
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/** 刷新token */
export const refreshTokenApi = (data?: object) => {
  return http.request<RefreshTokenResult>("post", "/refresh-token", { data });
};

/**
 * 获取验证码
 * @returns 验证码
 */
export const getCaptcha = () => {
  return http.get('/getCaptcha');
};

/**
 * 获取accessToken
 * @returns accessToken对象
 */
export const getAccessToken = (data: OAuthClientInfo, authorizationCode: string, state: string) => {
  const headers: any = {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
  headers.Authorization = `Basic ${base64Str(`${data.clientId}:${data.clientSecret}`)}`
  // 移除入参中的key
  delete data.clientId
  delete data.clientSecret
  // 封装参数
  const params = {
    grant_type: data.grantType,
    redirect_uri: data.redirectUri,
    code: authorizationCode,
    state
  }
  return http.post('/oauth2/token', {
    data: params,
    headers
  });
};

/**
 * 获取授权确认页面相关数据
 * @param queryString 查询参数，地址栏参数
 * @returns 授权确认页面相关数据
 */
export function getConsentParameters(queryString: string) {
  return http.get(`/oauth2/consent/parameters${queryString}`)
}

/**
 * 提交授权确认
 * @param data 客户端、scope等
 * @param requestUrl 请求地址(授权码与设备码授权提交不一样)
 * @returns 是否确认成功
 */
export function submitApproveScope(data: any, requestUrl: string) {
  return http.post(requestUrl, {
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 查询用户信息
 * @returns 用户信息
 */
export function getUserinfo() {
  return http.get('/user')
}

/**
 * 根据手机号获取短信验证码
 * @param phone 手机号
 * @returns 短信验证码
 */
export function getSmsCaptcha(phone: string) {
  return http.get(`/getSmsCaptcha?phone=${phone}`)
}

/**
 * 生成并获取二维码id
 * @returns 二维码id
 */
export function generateQrCode() {
  return http.get<null, GenerateQrCodeResult>(`/qrCode/login/generateQrCode`)
}

/**
 * 根据二维码id获取二维码信息
 * @param qrCodeId 二维码id
 * @returns 二维码信息
 */
export function fetchByQrCodeId(qrCodeId: string) {
  return http.get<null, QrCodeInfo>(`/qrCode/login/fetch/${qrCodeId}`)
}