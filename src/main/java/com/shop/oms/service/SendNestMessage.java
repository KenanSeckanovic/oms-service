package com.shop.oms.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SendNestMessage {
    private final static String QUEUE_NAME = "inventory_queue";

    public void sendMessage(String pattern, String data) throws Exception {

        // RabbitMQ Connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("password");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Queue deklarieren (muss zu Nest passen)
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            // Nachricht wie NestJS sie erwartet
            Map<String, Object> message = new HashMap<>();
            message.put("pattern", pattern);
            message.put("data", data);

            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(message);

            // Nachricht senden
            channel.basicPublish(
                    "",             // default exchange (!!!)
                    QUEUE_NAME,     // routing key = queue name
                    null,
                    jsonMessage.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("📤 Nachricht gesendet: " + jsonMessage);
        }
    }
}