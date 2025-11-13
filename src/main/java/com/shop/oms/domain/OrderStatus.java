package com.shop.oms.domain;
public enum OrderStatus { 
    RECEIVED, 
    RESERVED, 
    PAYMENT_OK, 
    PAYMENT_FAILED, 
    CANCELLED, 
    SENT_TO_WMS,
    COMPLETED 
}
