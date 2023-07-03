package com.rabbitmq.publisher.config;

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
import org.springframework.context.annotation.Primary;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class BlueGreenRabbitMQConfig {

    /**
     * <pre>
     *     Blue RabbitMQ 서버 연결 클래스.
     * </pre>
     */
    @Configuration
    public class BlueRabbitMQConfig {
        private final int port;
        private final String userName;
        private final String password;
        private final String virtualHost;
        private final String host;

        public BlueRabbitMQConfig(@Value("${mq.rabbitmq.blue.port}") int port
                , @Value("${mq.rabbitmq.blue.username}") String userName
                , @Value("${mq.rabbitmq.blue.password}") String password
                , @Value("${mq.rabbitmq.blue.virtual-host}") String virtualHost
                , @Value("${mq.rabbitmq.blue.host}") String host) {
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.virtualHost = virtualHost;
            this.host = host;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory blueSimpleRabbitListenerContainerFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
            simpleRabbitListenerContainerFactory.setConnectionFactory(this.blueConnectionFactory());
            simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
            simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());

            return simpleRabbitListenerContainerFactory;
        }

        @Primary
        @Bean
        public ConnectionFactory blueConnectionFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
            connectionFactory.setPort(this.port);
            connectionFactory.setUsername(this.userName);
            connectionFactory.setPassword(this.password);
            connectionFactory.setVirtualHost(this.virtualHost);
            connectionFactory.setUri("amqp://" + this.host);
            connectionFactory.setAutomaticRecoveryEnabled(false);

            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
            cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);

            return cachingConnectionFactory;
        }

        @Bean
        public AmqpAdmin blueAmqpAdmin() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            return new RabbitAdmin(blueConnectionFactory());
        }

        @Bean
        public RabbitTemplate blueRabbitTemplate(ConnectionFactory blueConnectionFactory) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(blueConnectionFactory);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));

            return rabbitTemplate;
        }
    }

    /**
     * <pre>
     *     Green RabbitMQ 서버 연결 클래스.
     * </pre>
     */
    @Configuration
    public class GreenRabbitMQConfig {
        private final int port;
        private final String userName;
        private final String password;
        private final String virtualHost;
        private final String host;

        public GreenRabbitMQConfig(@Value("${mq.rabbitmq.green.port}") int port
                , @Value("${mq.rabbitmq.green.username}") String userName
                , @Value("${mq.rabbitmq.green.password}") String password
                , @Value("${mq.rabbitmq.green.virtual-host}") String virtualHost
                , @Value("${mq.rabbitmq.green.host}") String host) {
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.virtualHost = virtualHost;
            this.host = host;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory greenSimpleRabbitListenerContainerFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
            simpleRabbitListenerContainerFactory.setConnectionFactory(this.greenConnectionFactory());
            simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
            simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());

            return simpleRabbitListenerContainerFactory;
        }

        @Bean
        public ConnectionFactory greenConnectionFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
            connectionFactory.setPort(this.port);
            connectionFactory.setUsername(this.userName);
            connectionFactory.setPassword(this.password);
            connectionFactory.setVirtualHost(this.virtualHost);
            connectionFactory.setUri("amqp://" + this.host);
            connectionFactory.setAutomaticRecoveryEnabled(false);

            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
            cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);

            return cachingConnectionFactory;
        }

        @Bean
        public AmqpAdmin greenAmqpAdmin() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
            return new RabbitAdmin(greenConnectionFactory());
        }

        @Bean
        public RabbitTemplate greenRabbitTemplate(ConnectionFactory blueConnectionFactory) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(blueConnectionFactory);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));

            return rabbitTemplate;
        }
    }
}
