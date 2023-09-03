// index.ts
import axios from "axios";
import type { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig, AxiosResponse } from "axios";

type Result<T> = {
    code: number;
    message: string;
    result: T;
};

// 导出Request类，可以用来自定义传递配置来创建实例
export class Request {
    // axios 实例
    instance: AxiosInstance;
    // 基础配置，url和超时时间
    baseConfig: AxiosRequestConfig = {};

    constructor(config: AxiosRequestConfig) {
        // 使用axios.create创建axios实例
        this.instance = axios.create(Object.assign(this.baseConfig, config));

        this.instance.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                // 一般会请求拦截里面加token，用于后端的验证
                const token: any = localStorage.getItem("accessToken")
                if (token) {
                    config.headers!.Authorization = `${token.token_type} ${token.access_token}`;
                }

                return config;
            },
            (err: any) => {
                // 请求错误，这里可以用全局提示框进行提示
                return Promise.reject(err);
            }
        );

        this.instance.interceptors.response.use(
            (res: AxiosResponse) => {
                // 直接返回res，当然你也可以只返回res.data
                // 系统如果有自定义code也可以在这里处理
                return res.data;
            },
            (err: any) => {
                if (err.code === 'ERR_NETWORK') {
                    return Promise.reject(err);
                }
                // 这里用来处理http常见错误，进行全局提示
                let messageText = "";
                switch (err.response.status) {
                    case 400:
                        messageText = "请求参数错误(400)";
                        break;
                    case 401:
                        messageText = "未授权，请重新登录(401)";
                        // 这里可以做清空storage并跳转到登录页的操作
                        break;
                    case 403:
                        messageText = "拒绝访问(403)";
                        break;
                    case 404:
                        messageText = "请求路径出错(404)";
                        break;
                    case 408:
                        messageText = "请求超时(408)";
                        break;
                    case 500:
                        messageText = "服务器错误(500)";
                        break;
                    case 501:
                        messageText = "服务未实现(501)";
                        break;
                    case 502:
                        messageText = "网络错误(502)";
                        break;
                    case 503:
                        messageText = "服务不可用(503)";
                        break;
                    case 504:
                        messageText = "网络超时(504)";
                        break;
                    case 505:
                        messageText = "HTTP版本不受支持(505)";
                        break;
                    default:
                        messageText = `连接出错(${err.response.status})!`;
                }
                err.response.statusText = messageText
                // 这里错误消息可以使用全局弹框展示出来
                // 比如element plus 可以使用 ElMessage
                // ElMessage({
                //   showClose: true,
                //   message: `${message}，请检查网络或联系管理员！`,
                //   type: "error",
                // });
                // 这里是AxiosError类型，所以一般我们只reject我们需要的响应即可
                return Promise.reject(err.response);
            }
        );
    }

    // 定义请求方法
    request<T>(config: AxiosRequestConfig): Promise<AxiosResponse<T>> {
        return this.instance.request(config);
    }

    get<T>(config?: AxiosRequestConfig<T>): Promise<AxiosResponse<T>> {
        return this.request<T>({ ...config, method: 'GET' })
    }
    post<T>(config?: AxiosRequestConfig<T>): Promise<AxiosResponse<T>> {
        return this.request<T>({ ...config, method: 'POST' })
    }
    delete<T>(config?: AxiosRequestConfig<T>): Promise<AxiosResponse<T>> {
        return this.request<T>({ ...config, method: 'DELETE' })
    }
    put<T>(config?: AxiosRequestConfig<T>): Promise<AxiosResponse<T>> {
        return this.request<T>({ ...config, method: 'put' })
    }
    patch<T>(config?: AxiosRequestConfig<T>): Promise<AxiosResponse<T>> {
        return this.request<T>({ ...config, method: 'PATCH' })
    }
}

// 默认导出Request实例
export default Request
