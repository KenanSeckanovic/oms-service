package com.shop.oms.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
  @NotBlank String orderId,
  @NotNull Customer customer,
  @NotEmpty List<Item> items,
  @NotNull BigDecimal totalAmount,
  @NotNull Address shippingAddress
) {
  public record Customer(@NotBlank String customerId, @NotBlank String prename, @NotBlank String name) {}
  public record Item(@NotBlank String productId, @Min(1) int quantity, @NotNull BigDecimal price) {}
  public record Address(@NotBlank String street, @NotBlank String city, @NotBlank String zipCode, @NotBlank String country) {}
}