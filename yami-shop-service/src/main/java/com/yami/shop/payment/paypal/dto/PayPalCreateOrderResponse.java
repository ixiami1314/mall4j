package com.yami.shop.payment.paypal.dto;

import lombok.Data;

/**
 * 创建 PayPal 订单响应
 * @author mall4j
 */
@Data
public class PayPalCreateOrderResponse {

    /**
     * PayPal 订单ID
     */
    private String orderId;

    /**
     * 支付批准链接
     */
    private String approveUrl;

    /**
     * 订单状态
     */
    private String status;
}
