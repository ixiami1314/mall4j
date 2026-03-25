package com.yami.shop.bean.payment;

/**
 * 支付结果
 * @author mall4j
 */
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getBizPayNo() {
        return bizPayNo;
    }

    public void setBizPayNo(String bizPayNo) {
        this.bizPayNo = bizPayNo;
    }

    public String getApproveUrl() {
        return approveUrl;
    }

    public void setApproveUrl(String approveUrl) {
        this.approveUrl = approveUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

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
