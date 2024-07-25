package io.buzzy.websockets.server.messaging.service;

import io.buzzy.common.messaging.model.UserStatusUpdateDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserStatusUpdateProducer extends BuzzyKafkaProducer<UserStatusUpdateDTO> {

    public UserStatusUpdateProducer(@Value("${buzzy-websockets-server.kafka.user-status-update.topic}") String topic,
                                    KafkaTemplate<String, UserStatusUpdateDTO> producer) {
        super(topic, producer);
    }
}
