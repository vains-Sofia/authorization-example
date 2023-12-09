import loginRequest from '../util/http/LoginRequest'

/**
 * 生成二维码
 */
export function generateQrCode() {
    return loginRequest.get<any>({
        url: '/qrCode/login/generateQrCode'
    })
}

/**
 * 获取二维码信息
 * @param qrCodeId 二维码id
 */
export function fetch(qrCodeId: string) {
    return loginRequest.get<any>({
        url: `/qrCode/login/fetch/${qrCodeId}`
    })
}