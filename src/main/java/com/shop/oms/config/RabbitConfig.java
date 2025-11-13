package com.shop.oms.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitConfig {

  @Bean
  public TopicExchange wmsExchange(@Value("${oms.wms.exchange}") String exchange) {
    return new TopicExchange(exchange, true, false);
  }

  @Bean
  public Queue wmsStatusQueue(@Value("${oms.wms.status-queue}") String queue) {
    return new Queue(queue, true);
  }

  @Bean
  public Binding wmsStatusBinding(Queue wmsStatusQueue, TopicExchange wmsExchange) {
    // WMS veröffentlicht z.B. unter "wms.status.*"
    return BindingBuilder.bind(wmsStatusQueue).to(wmsExchange).with("wms.status.#");
  }
}
