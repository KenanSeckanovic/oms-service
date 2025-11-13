package com.shop.oms.service;

import com.shop.oms.dto.PaymentRequest;
import com.shop.oms.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentClient {
  private final WebClient webClient;

  public PaymentClient(@Qualifier("paymentWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  /**
   * Ruft den Payment-Service auf: POST /api/payment/process
   * Erwartet Boolean im Body (true = bezahlt, false = abgelehnt).
   */
  public PaymentResponse charge(PaymentRequest req, String customerName) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("orderId",      req.getOrderId());
    payload.put("customerName", customerName);
    payload.put("amount",       req.getAmount());
    payload.put("currency",     "EUR");             // falls du dynamisch willst: in PaymentRequest aufnehmen
    payload.put("method",       req.getMethod());   // z.B. "CARD"

    Boolean ok = webClient.post()
        .uri("/api/payment/process")
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(Boolean.class)
        .onErrorResume(e -> Mono.just(false))
        .block();

    boolean success = Boolean.TRUE.equals(ok);
    return new PaymentResponse(
        success,
        success ? "Payment successful" : "Payment declined by Payment Service"
    );
  }
}
