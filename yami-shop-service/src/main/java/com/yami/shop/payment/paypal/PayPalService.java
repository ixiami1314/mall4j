package com.yami.shop.payment.paypal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderRequest;
import com.yami.shop.payment.paypal.dto.PayPalCreateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * PayPal API 服务类
 * 封装 PayPal REST API 调用
 * @author mall4j
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalService {

    private final PayPalConfig payPalConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 沙盒环境 API 基础 URL
     */
    private static final String SANDBOX_BASE_URL = "https://api-m.sandbox.paypal.com";

    /**
     * 生产环境 API 基础 URL
     */
    private static final String LIVE_BASE_URL = "https://api-m.paypal.com";

    /**
     * Access Token 缓存
     */
    private volatile String cachedAccessToken;
    private volatile long tokenExpireTime;

    /**
     * 获取 API 基础 URL
     */
    private String getBaseUrl() {
        return payPalConfig.isSandbox() ? SANDBOX_BASE_URL : LIVE_BASE_URL;
    }

    /**
     * 获取 OAuth2 Access Token
     * 使用 client_id:client_secret 进行 Basic Auth 认证
     */
    private String getAccessToken() {
        // 检查缓存的 token 是否有效（提前 60 秒过期）
        if (cachedAccessToken != null && System.currentTimeMillis() < (tokenExpireTime - 60000)) {
            return cachedAccessToken;
        }

        String url = getBaseUrl() + "/v1/oauth2/token";

        // 构建 Basic Auth 头
        String credentials = payPalConfig.getClientId() + ":" + payPalConfig.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        // 请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                cachedAccessToken = jsonNode.get("access_token").asText();
                int expiresIn = jsonNode.get("expires_in").asInt();
                tokenExpireTime = System.currentTimeMillis() + (expiresIn * 1000L);
                return cachedAccessToken;
            } else {
                throw new YamiShopBindException("获取 PayPal Access Token 失败");
            }
        } catch (Exception e) {
            log.error("获取 PayPal Access Token 失败", e);
            throw new YamiShopBindException("获取 PayPal Access Token 失败: " + e.getMessage());
        }
    }

    /**
     * 构建 API 请求头
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("PayPal-Request-Id", String.valueOf(System.currentTimeMillis()));
        return headers;
    }

    /**
     * 创建 PayPal 订单
     * @param request 创建订单请求
     * @return 创建订单响应
     */
    public PayPalCreateOrderResponse createOrder(PayPalCreateOrderRequest request) {
        String url = getBaseUrl() + "/v2/checkout/orders";

        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();

            // 设置 intent
            requestBody.put("intent", "CAPTURE");

            // 设置购买单元
            ArrayNode purchaseUnits = requestBody.putArray("purchase_units");
            ObjectNode purchaseUnit = purchaseUnits.addObject();
            purchaseUnit.put("reference_id", request.getPayNo());

            // 设置商品描述
            if (request.getBody() != null) {
                purchaseUnit.put("description", request.getBody());
            }

            // 设置金额
            ObjectNode amount = purchaseUnit.putObject("amount");
            amount.put("currency_code", request.getCurrency());
            amount.put("value", String.format("%.2f", request.getPayAmount()));

            // 设置支付链接
            ObjectNode applicationContext = requestBody.putObject("payment_source");
            // 使用 return_url 和 cancel_url
            String returnUrl = request.getReturnUrl() != null ? request.getReturnUrl() : payPalConfig.getReturnUrl();
            String cancelUrl = request.getCancelUrl() != null ? request.getCancelUrl() : payPalConfig.getCancelUrl();

            // 重新构建请求体，包含正确的 application_context
            ObjectNode fullRequestBody = objectMapper.createObjectNode();
            fullRequestBody.put("intent", "CAPTURE");

            ArrayNode pu = fullRequestBody.putArray("purchase_units");
            ObjectNode puItem = pu.addObject();
            puItem.put("reference_id", request.getPayNo());
            if (request.getBody() != null) {
                puItem.put("description", request.getBody());
            }
            ObjectNode amt = puItem.putObject("amount");
            amt.put("currency_code", request.getCurrency());
            amt.put("value", String.format("%.2f", request.getPayAmount()));

            // 添加 application_context
            ObjectNode appContext = fullRequestBody.putObject("application_context");
            appContext.put("return_url", returnUrl);
            appContext.put("cancel_url", cancelUrl);
            appContext.put("brand_name", "Mall4j");
            appContext.put("user_action", "PAY_NOW");

            HttpEntity<String> httpRequest = new HttpEntity<>(objectMapper.writeValueAsString(fullRequestBody), buildHeaders());

            log.info("创建 PayPal 订单请求: {}", objectMapper.writeValueAsString(fullRequestBody));

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);

            log.info("创建 PayPal 订单响应: status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                PayPalCreateOrderResponse orderResponse = new PayPalCreateOrderResponse();
                orderResponse.setOrderId(jsonNode.get("id").asText());
                orderResponse.setStatus(jsonNode.get("status").asText());

                // 提取 approve 链接
                JsonNode links = jsonNode.get("links");
                if (links != null && links.isArray()) {
                    for (JsonNode link : links) {
                        if ("approve".equals(link.get("rel").asText())) {
                            orderResponse.setApproveUrl(link.get("href").asText());
                            break;
                        }
                    }
                }

                return orderResponse;
            } else {
                throw new YamiShopBindException("创建 PayPal 订单失败: " + response.getStatusCode());
            }
        } catch (YamiShopBindException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建 PayPal 订单失败", e);
            throw new YamiShopBindException("创建 PayPal 订单失败: " + e.getMessage());
        }
    }

    /**
     * 捕获订单
     * @param orderId PayPal 订单 ID
     * @return 捕获结果
     */
    public Map<String, Object> captureOrder(String orderId) {
        String url = getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture";

        try {
            // 空请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            HttpEntity<String> httpRequest = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), buildHeaders());

            log.info("捕获 PayPal 订单请求: orderId={}", orderId);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);

            log.info("捕获 PayPal 订单响应: status={}, body={}", response.getStatusCode(), response.getBody());

            if ((response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK)
                    && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                Map<String, Object> result = new HashMap<>();
                result.put("orderId", jsonNode.get("id").asText());
                result.put("status", jsonNode.get("status").asText());

                // 提取支付信息
                JsonNode purchaseUnits = jsonNode.get("purchase_units");
                if (purchaseUnits != null && purchaseUnits.isArray() && purchaseUnits.size() > 0) {
                    JsonNode payments = purchaseUnits.get(0).get("payments");
                    if (payments != null) {
                        JsonNode captures = payments.get("captures");
                        if (captures != null && captures.isArray() && captures.size() > 0) {
                            JsonNode capture = captures.get(0);
                            result.put("captureId", capture.get("id").asText());

                            JsonNode amount = capture.get("amount");
                            if (amount != null) {
                                result.put("amount", amount.get("value").asText());
                                result.put("currency", amount.get("currency_code").asText());
                            }
                        }
                    }

                    // 获取 reference_id（内部支付流水号）
                    JsonNode referenceId = purchaseUnits.get(0).get("reference_id");
                    if (referenceId != null) {
                        result.put("referenceId", referenceId.asText());
                    }
                }

                return result;
            } else {
                throw new YamiShopBindException("捕获 PayPal 订单失败: " + response.getStatusCode());
            }
        } catch (YamiShopBindException e) {
            throw e;
        } catch (Exception e) {
            log.error("捕获 PayPal 订单失败", e);
            throw new YamiShopBindException("捕获 PayPal 订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * @param orderId PayPal 订单 ID
     * @return 订单详情
     */
    public Map<String, Object> getOrder(String orderId) {
        String url = getBaseUrl() + "/v2/checkout/orders/" + orderId;

        try {
            HttpEntity<String> httpRequest = new HttpEntity<>(null, buildHeaders());

            log.info("获取 PayPal 订单详情请求: orderId={}", orderId);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpRequest, String.class);

            log.info("获取 PayPal 订单详情响应: status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                Map<String, Object> result = new HashMap<>();
                result.put("orderId", jsonNode.get("id").asText());
                result.put("status", jsonNode.get("status").asText());
                result.put("intent", jsonNode.get("intent").asText());

                // 提取购买单元信息
                JsonNode purchaseUnits = jsonNode.get("purchase_units");
                if (purchaseUnits != null && purchaseUnits.isArray() && purchaseUnits.size() > 0) {
                    JsonNode firstUnit = purchaseUnits.get(0);
                    result.put("referenceId", firstUnit.get("reference_id").asText());

                    JsonNode amount = firstUnit.get("amount");
                    if (amount != null) {
                        result.put("amount", amount.get("value").asText());
                        result.put("currency", amount.get("currency_code").asText());
                    }
                }

                // 提取支付者信息
                JsonNode payer = jsonNode.get("payer");
                if (payer != null) {
                    result.put("payerId", payer.get("payer_id").asText());
                    JsonNode name = payer.get("name");
                    if (name != null) {
                        result.put("payerGivenName", name.get("given_name").asText());
                        result.put("payerSurname", name.get("surname").asText());
                    }
                    result.put("payerEmail", payer.get("email_address").asText());
                }

                return result;
            } else {
                throw new YamiShopBindException("获取 PayPal 订单详情失败: " + response.getStatusCode());
            }
        } catch (YamiShopBindException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取 PayPal 订单详情失败", e);
            throw new YamiShopBindException("获取 PayPal 订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 验证 Webhook 签名（简化版）
     * @param requestBody 请求体
     * @param headers 请求头
     * @return 是否验证通过
     */
    public boolean verifyWebhookSignature(String requestBody, Map<String, String> headers) {
        // TODO: 实现完整的 Webhook 签名验证
        // 参考: https://developer.paypal.com/docs/api/webhooks/v1/#verify-webhook-signature
        // 目前开发阶段返回 true
        log.info("Webhook 签名验证（开发模式，跳过验证）");
        return true;
    }
}
