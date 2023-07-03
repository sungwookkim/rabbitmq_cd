package com.rabbitmq.publisher.repo;

import com.rabbitmq.publisher.entity.MqInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MqConnectionRepository extends JpaRepository<MqInfo, String> {
}
