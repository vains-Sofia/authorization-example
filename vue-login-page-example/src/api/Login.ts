import loginRequest from '../util/http/LoginRequest'

/**
 * 从认证服务获取AccessToken
 * @param data 获取token入参
 * @returns 返回AccessToken对象
 */
export function getToken(data: any) {
    // 可以设置为AccessToken的类型
    return loginRequest.post<any>({
        url: '/oauth2/token',
        data,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
}

/**
 * 获取图片验证码
 * @returns 返回图片验证码信息
 */
export function getImageCaptcha() {
    return loginRequest.get<any>({
        url: '/getCaptcha'
    })
}

/**
 * 提交登录表单
 * @param data 登录表单数据
 * @returns 登录状态
 */
export function loginSubmit(data: any) {
    return loginRequest.post<any>({
        url: '/login',
        data,
        headers: {
            nonceId: data.nonceId,
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}

/**
 * 根据手机号获取短信验证码
 * @param params 手机号json，会被转为QueryString
 * @returns 登录状态
 */
export function getSmsCaptchaByPhone(params: any) {
    return loginRequest.get<any>({
        url: '/getSmsCaptcha',
        params
    })
}

/**
 * 获取授权确认页面相关数据
 * @param queryString 查询参数，地址栏参数
 * @returns 授权确认页面相关数据
 */
export function getConsentParameters(queryString: string) {
    return loginRequest.get<any>({
        url: `/oauth2/consent/parameters${queryString}`
    })
}

/**
 * 提交授权确认
 * @param data 客户端、scope等
 * @param requestUrl 请求地址(授权码与设备码授权提交不一样)
 * @param nonceId 保持登录状态
 * @returns 是否确认成功
 */
export function submitApproveScope(data: any, requestUrl: string, nonceId: string) {
    return loginRequest.post<any>({
        url: requestUrl,
        data,
        headers: {
            nonceId,
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}

/**
 * 验证设备码
 * @param data user_code，设备码
 * @param nonceId 保持登录状态
 * @returns 是否确认成功
 */
export function deviceVerification(data: any, nonceId: string) {
    return loginRequest.post<any>({
        url: `/oauth2/device_verification`,
        data,
        headers: {
            nonceId,
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}