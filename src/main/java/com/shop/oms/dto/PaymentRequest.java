package com.shop.oms.dto;

import java.math.BigDecimal;

/**
 * Request-Objekt, das an den Payment-Service geschickt wird.
 */
public class PaymentRequest {

    private String orderId;
    private BigDecimal amount;
    private String method; // z.B. "CREDIT_CARD", "PAYPAL", "FAKE"

    public PaymentRequest() {
    }

    public PaymentRequest(String orderId, BigDecimal amount, String method) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                '}';
    }
}
