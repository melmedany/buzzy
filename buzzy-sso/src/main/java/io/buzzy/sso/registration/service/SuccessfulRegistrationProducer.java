package io.buzzy.sso.registration.service;

import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SuccessfulRegistrationProducer extends BuzzyKafkaProducer<SuccessfulRegistrationDTO> {

    public SuccessfulRegistrationProducer(@Value("${buzzy-sso.kafka.successful-registration.topic}") String topic,
                                          KafkaTemplate<String, SuccessfulRegistrationDTO> producer) {
        super(topic, producer);
    }
}
