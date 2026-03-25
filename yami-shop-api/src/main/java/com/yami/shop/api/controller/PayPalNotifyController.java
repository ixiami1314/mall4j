package com.yami.shop.api.controller;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.payment.PaymentContext;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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

    @Value("${paypal.frontend-url:http://localhost:5173}")
    private String frontendUrl;

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
     * 用户支付成功返回（GET 请求，重定向到前端）
     */
    @GetMapping("/return")
    public RedirectView handleReturnGet(HttpServletRequest request) {
        String token = request.getParameter("token");
        log.info("PayPal payment return callback (GET), token={}", token);
        // 重定向到前端的 PayPal 回调页面
        String redirectUrl = frontendUrl + "/order/paypal-return?token=" + (token != null ? token : "");
        return new RedirectView(redirectUrl);
    }

    /**
     * 用户支付成功返回（POST 请求）
     */
    @PostMapping("/return")
    public RedirectView handleReturnPost(HttpServletRequest request) {
        String token = request.getParameter("token");
        log.info("PayPal payment return callback (POST), token={}", token);
        // 重定向到前端的 PayPal 回调页面
        String redirectUrl = frontendUrl + "/order/paypal-return?token=" + (token != null ? token : "");
        return new RedirectView(redirectUrl);
    }
}
