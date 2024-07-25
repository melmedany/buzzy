package io.buzzy.api.profile.service;

import io.buzzy.common.messaging.model.NewConnectionDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewConnectionAddedProducer extends BuzzyKafkaProducer<NewConnectionDTO> {

    public NewConnectionAddedProducer(@Value("${buzzy-api.kafka.new-connection.topic}") String topic,
                                      KafkaTemplate<String, NewConnectionDTO> producer) {
        super(topic, producer);
    }
}
