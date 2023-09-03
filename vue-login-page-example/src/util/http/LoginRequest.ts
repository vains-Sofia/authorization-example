import Request from './Http'

const loginRequest = new Request({
    // 认证服务地址
    baseURL: import.meta.env.VITE_OAUTH_ISSUER,
    timeout: 60 * 1000
})

export default loginRequest
