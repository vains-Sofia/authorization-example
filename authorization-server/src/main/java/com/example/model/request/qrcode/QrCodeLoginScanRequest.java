package com.example.model.request.qrcode;

import lombok.Data;

/**
 * 扫描二维码入参
 *
 * @author vains
 */
@Data
public class QrCodeLoginScanRequest {

    /**
     * 二维码id
     */
    private String qrCodeId;

}