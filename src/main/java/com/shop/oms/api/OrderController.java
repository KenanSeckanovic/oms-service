package com.shop.oms.api;

import com.shop.oms.dto.CreateOrderRequest;
import com.shop.oms.dto.CreateOrderResponse;
import com.shop.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService service;
  public OrderController(OrderService service) { this.service = service; }

  @PostMapping
  public CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
    return service.createOrder(req);
  }
}
