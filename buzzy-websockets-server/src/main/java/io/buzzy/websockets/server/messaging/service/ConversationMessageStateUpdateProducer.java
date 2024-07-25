package io.buzzy.websockets.server.messaging.service;

import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConversationMessageStateUpdateProducer extends BuzzyKafkaProducer<ConversationMessageUpdateDTO> {

    public ConversationMessageStateUpdateProducer(@Value("${buzzy-websockets-server.kafka.conversation-message-state.topic}") String topic,
                                                  KafkaTemplate<String, ConversationMessageUpdateDTO> producer) {
        super(topic, producer);
    }
}
