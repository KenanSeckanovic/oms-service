package com.shop.oms.repo;

import com.shop.oms.domain.Order;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@org.springframework.stereotype.Repository
public class InMemoryOrderRepository {
  private final Map<String, Order> store = new ConcurrentHashMap<>();
  public void save(Order order) { store.put(order.getOrderId(), order); }
  public Optional<Order> find(String id) { return Optional.ofNullable(store.get(id)); }
}
