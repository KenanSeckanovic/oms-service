package com.shop.oms.dto;

import com.shop.oms.domain.OrderStatus;

public record CreateOrderResponse(String orderId, OrderStatus status, String message) {}
