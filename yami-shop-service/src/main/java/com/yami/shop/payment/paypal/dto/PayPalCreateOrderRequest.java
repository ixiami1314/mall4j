package com.yami.shop.payment.paypal.dto;

import lombok.Data;

/**
 * 创建 PayPal 订单请求
 * @author mall4j
 */
@Data
public class PayPalCreateOrderRequest {

    /**
     * 内部支付流水号
     */
    private String payNo;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 支付金额
     */
    private Double payAmount;

    /**
     * 货币代码，默认 USD
     */
    private String currency = "USD";

    /**
     * 回调URL
     */
    private String returnUrl;

    /**
     * 取消URL
     */
    private String cancelUrl;
}
