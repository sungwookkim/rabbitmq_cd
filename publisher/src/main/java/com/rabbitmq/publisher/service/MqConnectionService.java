package com.rabbitmq.publisher.service;

import com.rabbitmq.publisher.config.RoutingConnectionConfig;

public interface MqConnectionService {
    RoutingConnectionConfig.MqRouting getConnectionName();
}
