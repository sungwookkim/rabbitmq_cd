package com.rabbitmq.consumer.listener;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ConsumerListener {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerListener.class);

    @RabbitListener(containerFactory = "simpleRabbitListenerContainerFactory"
            , bindings = @QueueBinding(value = @Queue(value = "test_queue")
                , exchange = @Exchange("test_exchange"), key = "key")
            , ackMode = "MANUAL")
    public void testListener(Map<String, Object> value, Channel channel
            , @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) throws IOException, InterruptedException {

        Thread.sleep(1000);

        logger.info("{}", value);
        channel.basicAck(tag, false);
    }
}
