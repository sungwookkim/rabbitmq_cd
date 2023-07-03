package com.rabbitmq.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RabbitMQConfig {
    private final int port;
    private final String userName;
    private final String password;
    private final String virtualHost;
    private final String host;

    public RabbitMQConfig(@Value("${mq.rabbitmq.port}") int port
            , @Value("${mq.rabbitmq.username}") String userName
            , @Value("${mq.rabbitmq.password}") String password
            , @Value("${mq.rabbitmq.virtual-host}") String virtualHost
            , @Value("${mq.rabbitmq.host}") String host) {
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.virtualHost = virtualHost;
        this.host = host;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(this.connectionFactory());
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());

        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setPort(this.port);
        connectionFactory.setUsername(this.userName);
        connectionFactory.setPassword(this.password);
        connectionFactory.setVirtualHost(this.virtualHost);
        connectionFactory.setUri("amqp://" + this.host);
        connectionFactory.setAutomaticRecoveryEnabled(false);

        return new CachingConnectionFactory(connectionFactory);
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        return new RabbitAdmin(connectionFactory());
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));

        return rabbitTemplate;
    }
}
