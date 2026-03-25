# PayPal 支付渠道设计方案

## 概述

为 mall4j 电商系统添加 PayPal 支付渠道，面向国际用户跨境购物场景。

## 需求确认

| 项目 | 选择 |
|------|------|
| 用户场景 | 国际用户跨境购物 |
| 集成方式 | 官方 PayPal-Java-Server-SDK |
| 支付流程 | PayPal Checkout（跳转式） |
| 测试环境 | 需要沙盒测试环境 |

## 模块架构

```
yami-shop-service/
└── src/main/java/com/yami/shop/payment/
    ├── PaymentStrategy.java              # 支付策略接口
    ├── PaymentContext.java               # 策略上下文（路由器）
    ├── PaymentResult.java                # 统一支付结果
    └── paypal/
        ├── PayPalConfig.java             # PayPal 配置类
        ├── PayPalPaymentStrategy.java    # PayPal 策略实现
        ├── PayPalService.java            # PayPal API 封装
        └── dto/
            ├── PayPalOrderRequest.java   # 创建订单请求
            └── PayPalOrderResponse.java  # 订单响应
```

```
yami-shop-api/
└── src/main/java/com/yami/shop/api/controller/
    └── PayPalController.java             # PayPal 支付端点
    └── PayPalNotifyController.java       # PayPal 回调处理
```

## 数据流

```
用户点击支付 → PayController → PaymentContext.route(payType)
→ PayPalPaymentStrategy.pay() → PayPalService.createOrder()
→ 返回 PayPal 跳转链接 → 用户支付 → PayPal 回调 → PayPalNotifyController
→ PaymentContext.handleCallback() → 订单状态更新
```

## 数据库变更

### PayType 枚举扩展

```java
// PayType.java
WECHATPAY(1, "微信支付"),
ALIPAY(2, "支付宝"),
PAYPAL(3, "PayPal");  // 新增
```

`tz_order_settlement` 表无需修改，`pay_type` 字段已支持 Integer，`biz_pay_no` 存储 PayPal order_id。

### 配置（application.yml）

```yaml
paypal:
  client-id: ${PAYPAL_CLIENT_ID}
  client-secret: ${PAYPAL_CLIENT_SECRET}
  mode: sandbox  # sandbox 或 live
  return-url: https://your-domain.com/pay/paypal/return
  cancel-url: https://your-domain.com/pay/paypal/cancel
  webhook-id: ${PAYPAL_WEBHOOK_ID}
```

## API 接口设计

### 前端调用接口

| 端点 | 方法 | 说明 |
|------|------|------|
| `/p/pay/paypal/create` | POST | 创建 PayPal 订单，返回支付链接 |
| `/p/pay/paypal/capture` | POST | 用户支付后捕获订单 |
| `/p/pay/paypal/cancel` | GET | 用户取消支付回调 |

### PayPal 回调接口

| 端点 | 方法 | 说明 |
|------|------|------|
| `/notice/pay/paypal/webhook` | POST | PayPal 事件通知回调 |

### 接口详情

**1. 创建订单请求/响应**

```json
// POST /p/pay/paypal/create
// Request
{
  "orderNumbers": "123456,789012"
}

// Response
{
  "code": 200,
  "data": {
    "payPalOrderId": "5O190127TN364715T",
    "approveUrl": "https://www.sandbox.paypal.com/checkoutnow?token=...",
    "payNo": "内部支付流水号"
  }
}
```

**2. 捕获订单**

```json
// POST /p/pay/paypal/capture
// Request
{
  "payPalOrderId": "5O190127TN364715T"
}

// Response
{
  "code": 200,
  "data": {
    "success": true,
    "orderNumbers": ["123456"]
  }
}
```

**3. Webhook 事件处理**

- `CHECKOUT.ORDER.APPROVED` → 订单已批准（可选捕获）
- `PAYMENT.CAPTURE.COMPLETED` → 支付完成 → 更新订单状态
- `PAYMENT.CAPTURE.DENIED` → 支付拒绝 → 记录日志

## 核心流程

### 支付流程时序图

```
┌────────┐    ┌───────────┐    ┌─────────────────┐    ┌───────────────┐    ┌────────┐
│ 用户   │    │PayController│   │PayPalPaymentStrategy│  │ PayPalService │    │PayPal  │
└───┬────┘    └─────┬─────┘    └────────┬────────┘    └──────┬────────┘    └───┬────┘
    │               │                    │                    │                │
    │ 1.点击支付    │                    │                    │                │
    │──────────────>│                    │                    │                │
    │               │ 2.pay(payType=3)   │                    │                │
    │               │───────────────────>│                    │                │
    │               │                    │ 3.createOrder()    │                │
    │               │                    │───────────────────>│                │
    │               │                    │                    │ 4.REST API     │
    │               │                    │                    │───────────────>│
    │               │                    │                    │ 5.order_id+url │
    │               │                    │                    │<───────────────│
    │               │                    │ 6.PayPalOrderResp  │                │
    │               │                    │<───────────────────│                │
    │ 7.返回approveUrl                  │                    │                │
    │<──────────────│                    │                    │                │
    │               │                    │                    │                │
    │ 8.跳转PayPal支付                   │                    │                │
    │─────────────────────────────────────────────────────────────────────────>│
    │ 9.用户完成支付                      │                    │                │
    │<─────────────────────────────────────────────────────────────────────────│
    │ 10.调用capture                      │                    │                │
    │──────────────>│                    │                    │                │
    │               │ 11.capture()       │                    │                │
    │               │───────────────────>│                    │                │
    │               │                    │ 12.captureOrder()  │                │
    │               │                    │───────────────────>│                │
    │               │                    │                    │ 13.确认捕获     │
    │               │                    │                    │───────────────>│
    │               │                    │ 14.success         │                │
    │               │                    │<───────────────────│                │
    │               │ 15.更新订单状态     │                    │                │
    │               │───────────────────>│ paySuccess()       │                │
    │ 16.支付成功   │                    │                    │                │
    │<──────────────│                    │                    │                │
```

### 关键代码逻辑

**PaymentStrategy 接口**：

```java
public interface PaymentStrategy {
    /** 创建支付，返回支付信息 */
    PaymentResult createPayment(PayInfoDto payInfo);

    /** 处理支付回调 */
    void handleCallback(HttpServletRequest request);

    /** 获取支持的支付类型 */
    PayType getPayType();
}
```

**PayPalPaymentStrategy 核心逻辑**：

```java
@Override
public PaymentResult createPayment(PayInfoDto payInfo) {
    // 1. 构建 PayPal OrderRequest
    // 2. 调用 PayPalService.createOrder()
    // 3. 返回 approveUrl 给前端
}

@Override
public void handleCallback(HttpServletRequest request) {
    // 1. 验证 Webhook 签名
    // 2. 解析事件类型
    // 3. 调用 payService.paySuccess()
}
```

## 文件清单

### 新增文件

| 文件路径 | 说明 |
|----------|------|
| `yami-shop-service/.../payment/PaymentStrategy.java` | 支付策略接口 |
| `yami-shop-service/.../payment/PaymentContext.java` | 策略上下文 |
| `yami-shop-service/.../payment/PaymentResult.java` | 支付结果 DTO |
| `yami-shop-service/.../payment/paypal/PayPalConfig.java` | PayPal 配置 |
| `yami-shop-service/.../payment/paypal/PayPalService.java` | PayPal API 封装 |
| `yami-shop-service/.../payment/paypal/PayPalPaymentStrategy.java` | PayPal 策略实现 |
| `yami-shop-service/.../payment/paypal/dto/PayPalOrderRequest.java` | 请求 DTO |
| `yami-shop-service/.../payment/paypal/dto/PayPalOrderResponse.java` | 响应 DTO |
| `yami-shop-api/.../controller/PayPalController.java` | 支付接口 |
| `yami-shop-api/.../controller/PayPalNotifyController.java` | 回调接口 |

### 修改文件

| 文件路径 | 变更内容 |
|----------|----------|
| `yami-shop-bean/.../enums/PayType.java` | 新增 `PAYPAL(3, "PayPal")` |
| `yami-shop-service/pom.xml` | 添加 PayPal SDK 依赖 |
| `yami-shop-api/src/main/resources/application.yml` | 添加 PayPal 配置项 |

### Maven 依赖

```xml
<dependency>
    <groupId>com.paypal</groupId>
    <artifactId>paypal-server-sdk</artifactId>
    <version>1.1.0</version>
</dependency>
```

## 实现顺序

1. **基础架构**：PaymentStrategy 接口、PaymentContext、PaymentResult
2. **配置层**：PayPalConfig、application.yml 配置
3. **服务层**：PayPalService（API 封装）、PayPalPaymentStrategy
4. **控制层**：PayPalController、PayPalNotifyController
5. **枚举扩展**：PayType 新增 PAYPAL
6. **测试验证**：沙盒环境测试

## 参考资料

- [PayPal-Java-Server-SDK (Official GitHub)](https://github.com/paypal/PayPal-Java-Server-SDK)
- [PayPal Orders API v2 Documentation](https://developer.paypal.com/docs/api/orders/v2/)
- [PayPal Checkout Standard Integration](https://developer.paypal.com/api/rest/integration/orders-api/api-use-cases/standard/)
