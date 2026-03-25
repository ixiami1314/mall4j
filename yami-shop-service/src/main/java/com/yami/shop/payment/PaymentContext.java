package com.yami.shop.payment;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.bean.payment.PaymentResult;
import com.yami.shop.common.exception.YamiShopBindException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付策略上下文（路由器）
 * @author mall4j
 */
@Component
public class PaymentContext {

    private final Map<PayType, PaymentStrategy> strategyMap;

    @Autowired
    public PaymentContext(List<PaymentStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(PaymentStrategy::getPayType, Function.identity()));
    }

    /**
     * 创建支付
     * @param payType 支付类型
     * @param payInfo 支付信息
     * @return 支付结果
     */
    public PaymentResult createPayment(PayType payType, PayInfoDto payInfo) {
        PaymentStrategy strategy = getStrategy(payType);
        return strategy.createPayment(payInfo);
    }

    /**
     * 处理回调
     * @param payType 支付类型
     * @param request HTTP请求
     */
    public void handleCallback(PayType payType, HttpServletRequest request) {
        PaymentStrategy strategy = getStrategy(payType);
        strategy.handleCallback(request);
    }

    /**
     * 获取策略
     * @param payType 支付类型
     * @return 支付策略
     */
    private PaymentStrategy getStrategy(PayType payType) {
        PaymentStrategy strategy = strategyMap.get(payType);
        if (strategy == null) {
            throw new YamiShopBindException("不支持的支付方式: " + payType.getPayTypeName());
        }
        return strategy;
    }
}
