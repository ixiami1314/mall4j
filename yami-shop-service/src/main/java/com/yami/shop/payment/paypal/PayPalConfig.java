package com.yami.shop.payment.paypal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PayPal 配置
 * @author mall4j
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {

    /**
     * PayPal Client ID
     */
    private String clientId;

    /**
     * PayPal Client Secret
     */
    private String clientSecret;

    /**
     * 运行模式: sandbox 或 live
     */
    private String mode = "sandbox";

    /**
     * 支付成功回调URL
     */
    private String returnUrl;

    /**
     * 取消支付回调URL
     */
    private String cancelUrl;

    /**
     * Webhook ID（用于验证签名）
     */
    private String webhookId;

    /**
     * 前端 URL（用于回调重定向）
     */
    private String frontendUrl;

    /**
     * 是否为沙盒环境
     */
    public boolean isSandbox() {
        return "sandbox".equalsIgnoreCase(mode);
    }
}
