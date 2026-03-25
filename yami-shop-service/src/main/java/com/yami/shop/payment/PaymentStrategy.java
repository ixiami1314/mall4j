package com.yami.shop.payment;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.bean.payment.PaymentResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 支付策略接口
 * @author mall4j
 */
public interface PaymentStrategy {

    /**
     * 创建支付
     * @param payInfo 支付信息
     * @return 支付结果
     */
    PaymentResult createPayment(PayInfoDto payInfo);

    /**
     * 处理支付回调
     * @param request HTTP请求
     */
    void handleCallback(HttpServletRequest request);

    /**
     * 获取支持的支付类型
     * @return 支付类型
     */
    PayType getPayType();
}
