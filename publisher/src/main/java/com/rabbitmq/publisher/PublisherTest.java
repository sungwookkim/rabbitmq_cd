package com.rabbitmq.publisher;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PublisherTest {
    private static int COUNT = 0;
    private final RabbitTemplate rabbitTemplate;

    public PublisherTest(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        send();
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask1() {
        send();
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask2() {
        send();
    }

    private void send() {
        String connectionServer = "blue";
        if(rabbitTemplate.getConnectionFactory().getPort() != 5674) {
            connectionServer = "green";
        }

        String value = String.format("{\"value\" : \"%s - %d\"}", connectionServer, COUNT++);
        Message message = MessageBuilder.withBody(value.getBytes(StandardCharsets.UTF_8))
                .build();

        rabbitTemplate.convertAndSend("test_exchange"
                , "key"
                , message);
    }
}
