import CryptoJS from 'crypto-js'

/**
 * 生成 CodeVerifier
 *
 * return CodeVerifier
 */
export function generateCodeVerifier() {
    return generateRandomString(32)
}

/**
 * 生成随机字符串
 * @param length 随机字符串的长度
 * @returns 随机字符串
 */
export function generateRandomString(length: number) {
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
export function generateCodeChallenge(code_verifier: string) {
    return base64URL(CryptoJS.SHA256(code_verifier))
}

/**
 * 将字符串base64加密后在转为url string
 * @param str 字符串
 * @returns bese64转码后转为url string
 */
export function base64URL(str: CryptoJS.lib.WordArray) {
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
export function base64Str(str: string) {
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(str));
}
