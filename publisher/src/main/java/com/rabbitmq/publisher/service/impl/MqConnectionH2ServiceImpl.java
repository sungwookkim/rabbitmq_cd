package com.rabbitmq.publisher.service.impl;

import com.rabbitmq.publisher.config.RoutingConnectionConfig;
import com.rabbitmq.publisher.repo.MqConnectionRepository;
import com.rabbitmq.publisher.service.MqConnectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MqConnectionH2ServiceImpl implements MqConnectionService {
    private final MqConnectionRepository mqConnectionRepository;

    public MqConnectionH2ServiceImpl(MqConnectionRepository mqConnectionRepository) {
        this.mqConnectionRepository = mqConnectionRepository;
    }

    @Transactional
    public RoutingConnectionConfig.MqRouting getConnectionName() {
        return RoutingConnectionConfig.MqRouting.eq(this.mqConnectionRepository.findAll().get(0).getMqConnName());
    }
}
