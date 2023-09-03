/**
 * 根据参数name获取地址栏的参数
 * @param name 地址栏参数的key
 * @returns key对用的值
 */
export function getQueryString(name: string) {
    const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')

    const r = window.location.search.substr(1).match(reg)

    if (r != null) {
        return decodeURIComponent(r[2])
    }

    return null
}