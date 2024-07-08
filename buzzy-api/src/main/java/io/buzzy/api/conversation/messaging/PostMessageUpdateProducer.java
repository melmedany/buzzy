package io.buzzy.api.conversation.messaging;

import io.buzzy.common.messaging.model.PostMessageUpdateDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostMessageUpdateProducer extends BuzzyKafkaProducer<PostMessageUpdateDTO> {

    public PostMessageUpdateProducer(@Value("${buzzy-api.kafka.post-message-update-topic}") String topic,
                                      KafkaTemplate<String, PostMessageUpdateDTO> producer) {
        super(topic, producer);
    }
}
