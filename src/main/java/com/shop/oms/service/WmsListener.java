package com.shop.oms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WmsListener {
  private static final Logger log = LoggerFactory.getLogger(WmsListener.class);

  // legt die Queue automatisch an, falls sie nicht existiert
  @RabbitListener(queues = "${oms.wms.status-queue}")
  public void onStatusUpdate(java.util.Map<String, Object> event) {
  log.info("WMS status: {}", event);
  }
}
