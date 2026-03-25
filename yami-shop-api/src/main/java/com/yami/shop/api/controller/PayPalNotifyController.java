package com.yami.shop.api.controller;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.payment.PaymentContext;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PayPal 支付回调控制器
 * @author mall4j
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/notice/pay/paypal")
@AllArgsConstructor
public class PayPalNotifyController {

    private final PaymentContext paymentContext;

    /**
     * PayPal Webhook 回调
     */
    @PostMapping("/webhook")
    public String handleWebhook(HttpServletRequest request) {
        log.info("Received PayPal webhook callback");
        try {
            paymentContext.handleCallback(PayType.PAYPAL, request);
            return "success";
        } catch (Exception e) {
            log.error("Failed to process PayPal webhook", e);
            return "fail";
        }
    }

    /**
     * 用户支付成功返回页面
     */
    @PostMapping("/return")
    public String handleReturn(HttpServletRequest request) {
        log.info("PayPal payment return callback");
        try {
            paymentContext.handleCallback(PayType.PAYPAL, request);
            return "success";
        } catch (Exception e) {
            log.error("Failed to process PayPal return", e);
            return "fail";
        }
    }
}
