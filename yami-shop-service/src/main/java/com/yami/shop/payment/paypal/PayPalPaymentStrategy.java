package com.yami.shop.payment.paypal;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.bean.payment.PaymentResult;
import com.yami.shop.payment.PaymentStrategy;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderRequest;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderResponse;
import com.yami.shop.service.PayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * PayPal 支付策略实现
 * @author mall4j
 */
@Slf4j
@Component
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Autowired
    private PayPalConfig payPalConfig;

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private PayService payService;

    @Override
    public PaymentResult createPayment(PayInfoDto payInfo) {
        // 构建 PayPal 订单请求
        PayPalCreateOrderRequest request = new PayPalCreateOrderRequest();
        request.setPayNo(payInfo.getPayNo());
        request.setBody(payInfo.getBody());
        request.setPayAmount(payInfo.getPayAmount());
        request.setCurrency("USD");
        request.setReturnUrl(payPalConfig.getReturnUrl());
        request.setCancelUrl(payPalConfig.getCancelUrl());

        // 调用 PayPal API 创建订单
        PayPalCreateOrderResponse response = payPalService.createOrder(request);

        return PaymentResult.success(
            payInfo.getPayNo(),
            response.getOrderId(),
            response.getApproveUrl()
        );
    }

    @Override
    public void handleCallback(HttpServletRequest request) {
        // 从请求中获取 PayPal 订单ID
        String orderId = request.getParameter("token");
        if (orderId == null || orderId.isEmpty()) {
            orderId = request.getParameter("order_id");
        }

        if (orderId == null) {
            log.error("PayPal callback missing order ID");
            return;
        }

        log.info("Processing PayPal callback for order: {}", orderId);

        // 获取订单详情
        Map<String, Object> order = payPalService.getOrder(orderId);
        List<Map<String, Object>> purchaseUnits = (List<Map<String, Object>>) order.get("purchase_units");
        String payNo = (String) purchaseUnits.get(0).get("reference_id");

        // 捕获订单
        Map<String, Object> captureResult = payPalService.captureOrder(orderId);
        boolean success = "COMPLETED".equals(captureResult.get("status"));

        if (success) {
            // 更新订单状态
            List<String> orderNumbers = payService.paySuccess(payNo, orderId);
            log.info("PayPal payment completed: payNo={}, orderNumbers={}", payNo, orderNumbers);
        } else {
            log.error("PayPal payment capture failed: orderId={}", orderId);
        }
    }

    @Override
    public PayType getPayType() {
        return PayType.PAYPAL;
    }
}
