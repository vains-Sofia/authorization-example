package com.example.controller;

import com.example.model.Result;
import com.example.model.request.qrcode.QrCodeLoginConsentRequest;
import com.example.model.request.qrcode.QrCodeLoginScanRequest;
import com.example.model.response.qrcode.QrCodeGenerateResponse;
import com.example.model.response.qrcode.QrCodeLoginFetchResponse;
import com.example.model.response.qrcode.QrCodeLoginScanResponse;
import com.example.service.IQrCodeLoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 二维码登录接口
 *
 * @author vains
 */
@RestController
@AllArgsConstructor
@RequestMapping("/qrCode")
public class QrCodeLoginController {

    private final IQrCodeLoginService iQrCodeLoginService;

    @GetMapping("/login/generateQrCode")
    public Result<QrCodeGenerateResponse> generateQrCode() {
        // 生成二维码
        return Result.success(iQrCodeLoginService.generateQrCode());
    }

    @GetMapping("/login/fetch/{qrCodeId}")
    public Result<QrCodeLoginFetchResponse> fetch(@PathVariable String qrCodeId) {
        // 轮询二维码状态
        return Result.success(iQrCodeLoginService.fetch(qrCodeId));
    }


    @PostMapping("/scan")
    public Result<QrCodeLoginScanResponse> scan(@RequestBody QrCodeLoginScanRequest loginScan) {
        // app 扫码二维码
        return Result.success(iQrCodeLoginService.scan(loginScan));
    }

    @PostMapping("/consent")
    public Result<String> consent(@RequestBody QrCodeLoginConsentRequest loginConsent) {

        // app 确认登录
        iQrCodeLoginService.consent(loginConsent);

        return Result.success();
    }

}