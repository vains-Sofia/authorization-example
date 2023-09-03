import Request from './Http'

const request = new Request({
    // 网关或其它后端服务地址
    baseURL: '',
    timeout: 60 * 1000
})

export default request
