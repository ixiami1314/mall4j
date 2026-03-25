package com.yami.shop.api.controller;

import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.bean.payment.PaymentResult;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.payment.PaymentContext;
import com.yami.shop.security.api.model.YamiUser;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * PayPal 支付控制器
 * @author mall4j
 */
@Slf4j
@RestController
@RequestMapping("/p/pay/paypal")
@Tag(name = "PayPal支付接口")
@AllArgsConstructor
public class PayPalController {

    private final PayService payService;
    private final PaymentContext paymentContext;

    /**
     * 创建 PayPal 订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建PayPal订单", description = "创建PayPal订单并返回支付链接")
    public ServerResponseEntity<Map<String, String>> createOrder(@RequestBody PayParam payParam) {
        YamiUser user = SecurityUtils.getUser();
        String userId = user.getUserId();

        // 设置支付类型为 PayPal
        payParam.setPayType(PayType.PAYPAL.value());

        // 获取支付信息
        PayInfoDto payInfo = payService.pay(userId, payParam);

        // 调用 PayPal 策略创建支付
        PaymentResult result = paymentContext.createPayment(PayType.PAYPAL, payInfo);

        if (!result.isSuccess()) {
            return ServerResponseEntity.showFailMsg(result.getErrorMessage());
        }

        Map<String, String> data = new HashMap<>();
        data.put("payPalOrderId", result.getBizPayNo());
        data.put("approveUrl", result.getApproveUrl());
        data.put("payNo", result.getPayNo());

        return ServerResponseEntity.success(data);
    }

    /**
     * 捕获订单（用户支付后调用）
     */
    @PostMapping("/capture")
    @Operation(summary = "捕获PayPal订单", description = "用户在PayPal完成支付后调用")
    public ServerResponseEntity<Map<String, Object>> captureOrder(@RequestBody Map<String, String> params) {
        String payPalOrderId = params.get("payPalOrderId");
        if (payPalOrderId == null || payPalOrderId.isEmpty()) {
            return ServerResponseEntity.showFailMsg("PayPal订单ID不能为空");
        }

        // 这里简化处理，实际捕获逻辑在 PayPalNotifyController 的回调中执行
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("payPalOrderId", payPalOrderId);

        return ServerResponseEntity.success(data);
    }

    /**
     * 取消支付回调
     */
    @GetMapping("/cancel")
    @Operation(summary = "取消PayPal支付", description = "用户取消支付后的回调")
    public ServerResponseEntity<Void> cancelPayment() {
        log.info("PayPal payment cancelled by user");
        return ServerResponseEntity.success();
    }
}
