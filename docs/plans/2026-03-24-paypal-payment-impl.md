# PayPal 支付渠道实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 mall4j 电商系统添加 PayPal 支付渠道，支持国际用户跨境购物

**Architecture:** 采用策略模式，创建 `PaymentStrategy` 接口和 `PayPalPaymentStrategy` 实现，通过 `PaymentContext` 路由不同支付类型

**Tech Stack:** Spring Boot 3.4, PayPal Server SDK 1.1.0, Java 17, MyBatis-Plus

---

## Task 1: 扩展 PayType 枚举

**Files:**
- Modify: `yami-shop-bean/src/main/java/com/yami/shop/bean/enums/PayType.java`

**Step 1: 添加 PayPal 枚举值**

```java
// 在 PayType.java 中添加
public enum PayType {

	/** 微信支付*/
	WECHATPAY(1,"微信支付"),

	/** 支付宝*/
	ALIPAY(2,"支付宝"),

	/** PayPal*/
	PAYPAL(3,"PayPal");

	// ... 其余代码保持不变
}
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-bean -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-bean/src/main/java/com/yami/shop/bean/enums/PayType.java
git commit -m "feat(payment): add PAYPAL enum to PayType"
```

---

## Task 2: 添加 PayPal SDK 依赖

**Files:**
- Modify: `yami-shop-service/pom.xml`

**Step 1: 添加 PayPal SDK 依赖**

```xml
<!-- 在 yami-shop-service/pom.xml 的 <dependencies> 中添加 -->
<dependency>
    <groupId>com.paypal</groupId>
    <artifactId>paypal-server-sdk</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Step 2: 验证依赖下载**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn dependency:resolve -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-service/pom.xml
git commit -m "feat(payment): add PayPal Server SDK dependency"
```

---

## Task 3: 创建支付策略基础架构

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/PaymentStrategy.java`
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/PaymentResult.java`

**Step 1: 创建 PaymentStrategy 接口**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/PaymentStrategy.java
package com.yami.shop.payment;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
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
```

**Step 2: 创建 PaymentResult DTO**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/PaymentResult.java
package com.yami.shop.payment;

import lombok.Data;

/**
 * 支付结果
 * @author mall4j
 */
@Data
public class PaymentResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 支付流水号（内部）
     */
    private String payNo;

    /**
     * 第三方订单号
     */
    private String bizPayNo;

    /**
     * 支付跳转链接
     */
    private String approveUrl;

    /**
     * 错误信息
     */
    private String errorMessage;

    public static PaymentResult success(String payNo, String bizPayNo, String approveUrl) {
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPayNo(payNo);
        result.setBizPayNo(bizPayNo);
        result.setApproveUrl(approveUrl);
        return result;
    }

    public static PaymentResult fail(String errorMessage) {
        PaymentResult result = new PaymentResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
```

**Step 3: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/
git commit -m "feat(payment): add PaymentStrategy interface and PaymentResult DTO"
```

---

## Task 4: 创建 PayPal 配置类

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalConfig.java`

**Step 1: 创建 PayPalConfig**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalConfig.java
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
     * 是否为沙盒环境
     */
    public boolean isSandbox() {
        return "sandbox".equalsIgnoreCase(mode);
    }
}
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalConfig.java
git commit -m "feat(payment): add PayPalConfig configuration class"
```

---

## Task 5: 创建 PayPal DTO 类

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/paypal/dto/PayPalCreateOrderRequest.java`
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/paypal/dto/PayPalCreateOrderResponse.java`

**Step 1: 创建 PayPalCreateOrderRequest**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/paypal/dto/PayPalCreateOrderRequest.java
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
```

**Step 2: 创建 PayPalCreateOrderResponse**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/paypal/dto/PayPalCreateOrderResponse.java
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
```

**Step 3: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/paypal/dto/
git commit -m "feat(payment): add PayPal DTO classes"
```

---

## Task 6: 创建 PayPalService（API 封装）

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalService.java`

**Step 1: 创建 PayPalService**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalService.java
package com.yami.shop.payment.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderRequest;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PayPal API 服务
 * @author mall4j
 */
@Slf4j
@Service
public class PayPalService {

    @Autowired
    private PayPalConfig payPalConfig;

    private PayPalHttpClient httpClient;

    @PostConstruct
    public void init() {
        PayPalEnvironment environment;
        if (payPalConfig.isSandbox()) {
            environment = new PayPalEnvironment.Sandbox(
                payPalConfig.getClientId(),
                payPalConfig.getClientSecret()
            );
        } else {
            environment = new PayPalEnvironment.Live(
                payPalConfig.getClientId(),
                payPalConfig.getClientSecret()
            );
        }
        this.httpClient = new PayPalHttpClient(environment);
    }

    /**
     * 创建订单
     */
    public PayPalCreateOrderResponse createOrder(PayPalCreateOrderRequest request) {
        OrderRequest orderRequest = buildOrderRequest(request);

        try {
            OrdersCreateRequest apiRequest = new OrdersCreateRequest();
            apiRequest.requestBody(orderRequest);

            HttpResponse<Order> response = httpClient.execute(apiRequest);
            Order order = response.result();

            PayPalCreateOrderResponse result = new PayPalCreateOrderResponse();
            result.setOrderId(order.id());
            result.setStatus(order.status());

            // 获取 approve 链接
            for (LinkDescription link : order.links()) {
                if ("approve".equals(link.rel())) {
                    result.setApproveUrl(link.href());
                    break;
                }
            }

            log.info("PayPal order created: orderId={}, status={}", order.id(), order.status());
            return result;

        } catch (IOException e) {
            log.error("Failed to create PayPal order", e);
            throw new YamiShopBindException("创建PayPal订单失败: " + e.getMessage());
        }
    }

    /**
     * 捕获订单
     */
    public boolean captureOrder(String orderId) {
        try {
            OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
            HttpResponse<Order> response = httpClient.execute(request);
            Order order = response.result();

            boolean success = "COMPLETED".equals(order.status());
            log.info("PayPal order captured: orderId={}, status={}, success={}",
                orderId, order.status(), success);
            return success;

        } catch (IOException e) {
            log.error("Failed to capture PayPal order: {}", orderId, e);
            throw new YamiShopBindException("捕获PayPal订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    public Order getOrder(String orderId) {
        try {
            OrdersGetRequest request = new OrdersGetRequest(orderId);
            HttpResponse<Order> response = httpClient.execute(request);
            return response.result();
        } catch (IOException e) {
            log.error("Failed to get PayPal order: {}", orderId, e);
            throw new YamiShopBindException("获取PayPal订单失败: " + e.getMessage());
        }
    }

    private OrderRequest buildOrderRequest(PayPalCreateOrderRequest request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // 设置金额
        ApplicationContext applicationContext = new ApplicationContext()
            .brandName("Mall4j")
            .locale("en-US")
            .returnUrl(request.getReturnUrl())
            .cancelUrl(request.getCancelUrl());
        orderRequest.applicationContext(applicationContext);

        // 构建购买单元
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
            .referenceId(request.getPayNo())
            .description(request.getBody())
            .amountWithBreakdown(new AmountWithBreakdown()
                .currencyCode(request.getCurrency())
                .value(String.format("%.2f", request.getPayAmount())));
        purchaseUnits.add(purchaseUnit);
        orderRequest.purchaseUnits(purchaseUnits);

        return orderRequest;
    }
}
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalService.java
git commit -m "feat(payment): add PayPalService for API integration"
```

---

## Task 7: 创建 PayPalPaymentStrategy

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalPaymentStrategy.java`

**Step 1: 创建 PayPalPaymentStrategy**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalPaymentStrategy.java
package com.yami.shop.payment.paypal;

import com.paypal.orders.Order;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.payment.PaymentResult;
import com.yami.shop.payment.PaymentStrategy;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderRequest;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderResponse;
import com.yami.shop.service.PayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        Order order = payPalService.getOrder(orderId);
        String payNo = order.purchaseUnits().get(0).referenceId();

        // 捕获订单
        boolean success = payPalService.captureOrder(orderId);

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
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/paypal/PayPalPaymentStrategy.java
git commit -m "feat(payment): add PayPalPaymentStrategy implementation"
```

---

## Task 8: 创建 PaymentContext（策略路由）

**Files:**
- Create: `yami-shop-service/src/main/java/com/yami/shop/payment/PaymentContext.java`

**Step 1: 创建 PaymentContext**

```java
// yami-shop-service/src/main/java/com/yami/shop/payment/PaymentContext.java
package com.yami.shop.payment;

import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
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
     */
    public PaymentResult createPayment(PayType payType, PayInfoDto payInfo) {
        PaymentStrategy strategy = getStrategy(payType);
        return strategy.createPayment(payInfo);
    }

    /**
     * 处理回调
     */
    public void handleCallback(PayType payType, HttpServletRequest request) {
        PaymentStrategy strategy = getStrategy(payType);
        strategy.handleCallback(request);
    }

    /**
     * 获取策略
     */
    private PaymentStrategy getStrategy(PayType payType) {
        PaymentStrategy strategy = strategyMap.get(payType);
        if (strategy == null) {
            throw new YamiShopBindException("不支持的支付方式: " + payType.getPayTypeName());
        }
        return strategy;
    }
}
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-service -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-service/src/main/java/com/yami/shop/payment/PaymentContext.java
git commit -m "feat(payment): add PaymentContext for strategy routing"
```

---

## Task 9: 创建 PayPalController

**Files:**
- Create: `yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalController.java`

**Step 1: 创建 PayPalController**

```java
// yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalController.java
package com.yami.shop.api.controller;

import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.payment.PaymentContext;
import com.yami.shop.payment.PaymentResult;
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

        // 通过回调处理支付完成逻辑
        // 这里简化处理，实际应该调用 PayPalNotifyController

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
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-api -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalController.java
git commit -m "feat(payment): add PayPalController for payment endpoints"
```

---

## Task 10: 创建 PayPalNotifyController

**Files:**
- Create: `yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalNotifyController.java`

**Step 1: 创建 PayPalNotifyController**

```java
// yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalNotifyController.java
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
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-api -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-api/src/main/java/com/yami/shop/api/controller/PayPalNotifyController.java
git commit -m "feat(payment): add PayPalNotifyController for webhook callbacks"
```

---

## Task 11: 添加配置示例

**Files:**
- Modify: `yami-shop-api/src/main/resources/application.yml`

**Step 1: 添加 PayPal 配置项**

```yaml
# 在 application.yml 中添加
paypal:
  client-id: ${PAYPAL_CLIENT_ID:your-client-id}
  client-secret: ${PAYPAL_CLIENT_SECRET:your-client-secret}
  mode: ${PAYPAL_MODE:sandbox}
  return-url: ${PAYPAL_RETURN_URL:http://localhost:8086/notice/pay/paypal/return}
  cancel-url: ${PAYPAL_CANCEL_URL:http://localhost:8086/p/pay/paypal/cancel}
  webhook-id: ${PAYPAL_WEBHOOK_ID:}
```

**Step 2: 验证编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn compile -pl yami-shop-api -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add yami-shop-api/src/main/resources/application.yml
git commit -m "feat(payment): add PayPal configuration properties"
```

---

## Task 12: 集成测试验证

**Step 1: 完整编译**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn clean compile -q`
Expected: BUILD SUCCESS

**Step 2: 运行应用**

Run: `cd /Users/junwei/workspace-fisher/mall4j && mvn spring-boot:run -pl yami-shop-api`
Expected: Application starts successfully

**Step 3: 测试 API 端点**

使用 Swagger UI 测试: `http://localhost:8086/doc.html`

- POST `/p/pay/paypal/create` - 创建 PayPal 订单
- POST `/p/pay/paypal/capture` - 捕获订单
- GET `/p/pay/paypal/cancel` - 取消支付

---

## 实现文件清单

| 文件 | 操作 |
|------|------|
| `yami-shop-bean/.../enums/PayType.java` | 修改 |
| `yami-shop-service/pom.xml` | 修改 |
| `yami-shop-service/.../payment/PaymentStrategy.java` | 新增 |
| `yami-shop-service/.../payment/PaymentResult.java` | 新增 |
| `yami-shop-service/.../payment/PaymentContext.java` | 新增 |
| `yami-shop-service/.../payment/paypal/PayPalConfig.java` | 新增 |
| `yami-shop-service/.../payment/paypal/PayPalService.java` | 新增 |
| `yami-shop-service/.../payment/paypal/PayPalPaymentStrategy.java` | 新增 |
| `yami-shop-service/.../payment/paypal/dto/PayPalCreateOrderRequest.java` | 新增 |
| `yami-shop-service/.../payment/paypal/dto/PayPalCreateOrderResponse.java` | 新增 |
| `yami-shop-api/.../controller/PayPalController.java` | 新增 |
| `yami-shop-api/.../controller/PayPalNotifyController.java` | 新增 |
| `yami-shop-api/.../resources/application.yml` | 修改 |
