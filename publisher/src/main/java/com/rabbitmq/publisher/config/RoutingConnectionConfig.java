package com.rabbitmq.publisher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.publisher.service.MqConnectionService;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RoutingConnectionConfig {
    private final MqConnectionService mqConnectionH2ServiceImpl;

    private final ConnectionFactory blueConnectionFactory;
    private final ConnectionFactory greenConnectionFactory;

    public RoutingConnectionConfig(MqConnectionService mqConnectionH2ServiceImpl
            , @Qualifier("blueConnectionFactory") ConnectionFactory blueConnectionFactory
            , @Qualifier("greenConnectionFactory") ConnectionFactory greenConnectionFactory) {
        this.mqConnectionH2ServiceImpl = mqConnectionH2ServiceImpl;
        this.blueConnectionFactory = blueConnectionFactory;
        this.greenConnectionFactory = greenConnectionFactory;
    }

    /**
     * <pre>
     *     라우팅이 가능한 RabbitTemplate 객체를 반환.
     * </pre>
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        // 라우팅에 사용될 RabbitMQ 접속 객체 설정
        Map<Object, ConnectionFactory> connectionFactoryMap = new HashMap<>();
        connectionFactoryMap.put(MqRouting.BLUE, this.blueConnectionFactory);
        connectionFactoryMap.put(MqRouting.GREEN, this.greenConnectionFactory);

        // 라우팅 설정이 되어 있는 ConnectionFactory 설정
        RoutingConnectionFactory routingConnectionFactory = new RoutingConnectionFactory(this.mqConnectionH2ServiceImpl);
        routingConnectionFactory.setTargetConnectionFactories(connectionFactoryMap);
        routingConnectionFactory.setDefaultTargetConnectionFactory(this.blueConnectionFactory);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(routingConnectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));

        return rabbitTemplate;
    }

    /**
     * <pre>
     *     라우팅 시 어떤 RabbitMQ를 사용할지 분기 처리하는 클래스.
     * </pre>
     */
    protected class RoutingConnectionFactory extends AbstractRoutingConnectionFactory {
        private final MqConnectionService mqConnectionH2ServiceImpl;

        public RoutingConnectionFactory(MqConnectionService mqConnectionH2ServiceImpl) {
            this.mqConnectionH2ServiceImpl = mqConnectionH2ServiceImpl;
        }

        /**
         * <pre>
         *     반환 하는 값에 따라 사용될 ConnectionFactory가 결정된다.
         *
         *     해당 메서드는 setTargetConnectionFactories 메서드와 연관 관계가 있다.
         *     setTargetConnectionFactories 메서드에 설명하자면 해당 메서드는 Map<Object, ConnectionFactory> 타입인 객체를
         *     매개변수로 받는데 determineCurrentLookupKey 메서드가 반환하는 값이
         *     setTargetConnectionFactories 메서드가 전달 받은 매개변수 객체의 Key 중 하나여야 한다.
         * </pre>
         */
        @Override
        protected Object determineCurrentLookupKey() {
            return this.mqConnectionH2ServiceImpl.getConnectionName();
        }
    }

    public enum MqRouting {
        BLUE("blue")
        , GREEN("green");

        String value;

        MqRouting(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static MqRouting eq(String routingServerName) {
            MqRouting[] mqRoutings = MqRouting.values();

            for(MqRouting mqRouting : mqRoutings) {
                if(routingServerName.equals(mqRouting.getValue())) {
                    return mqRouting;
                }
            }

            throw new IllegalStateException("라우팅 접속 정보가 올바르지 않습니다.");
        }
    }
}
