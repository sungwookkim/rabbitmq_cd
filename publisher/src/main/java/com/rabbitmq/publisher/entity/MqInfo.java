package com.rabbitmq.publisher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "mq_info")
public class MqInfo {

    @Id
    private String mqConnName;

    public MqInfo() {
    }

    public MqInfo(String mqConnName) {
        this.mqConnName = mqConnName;
    }

    public String getMqConnName() {
        return mqConnName;
    }
}
