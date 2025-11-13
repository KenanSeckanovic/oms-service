package com.shop.oms.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

@Component
public class WmsListener {
  private static final Logger log = LoggerFactory.getLogger(WmsListener.class);

  @RabbitListener(queues = "${oms.wms.status-queue}")
  public void onStatusUpdate(java.util.Map<String, Object> event) {
    // z.B. { "type":"STATUS", "status":"Items Picked", "orderId":"..." }
    log.info("WMS status: {}", event);
  }
}
