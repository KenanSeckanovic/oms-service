package com.shop.oms.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GrpcConfig {
  @Bean(destroyMethod = "shutdownNow")
  ManagedChannel inventoryChannel(
    @Value("${oms.inventory.host}") String host,
    @Value("${oms.inventory.port}") int port
  ) {
    return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
  }
}
