package io.buzzy.api.conversation.messaging;

import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import io.buzzy.common.messaging.model.producer.BuzzyKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConversationMessageUpdateProducer extends BuzzyKafkaProducer<ConversationMessageUpdateDTO> {

    public ConversationMessageUpdateProducer(@Value("${buzzy-api.kafka.message-update.topic}") String topic,
                                             KafkaTemplate<String, ConversationMessageUpdateDTO> producer) {
        super(topic, producer);
    }
}
