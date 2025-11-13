package com.shop.oms.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class WmsPublisher {
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingKey;

  public WmsPublisher(RabbitTemplate rabbitTemplate,
                      @Value("${oms.wms.exchange}") String exchange,
                      @Value("${oms.wms.routing-key:oms.wms.command}") String routingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  public void sendFulfillmentCommand(String orderId) {
    var message = java.util.Map.of("type","FULFILL_ORDER","orderId",orderId);
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }
}
