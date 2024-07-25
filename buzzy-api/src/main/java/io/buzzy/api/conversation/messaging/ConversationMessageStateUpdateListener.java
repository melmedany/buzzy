package io.buzzy.api.conversation.messaging;

import io.buzzy.api.conversation.service.ConversationMessageService;
import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import io.buzzy.common.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConversationMessageStateUpdateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageStateUpdateListener.class);

    private final ConversationMessageService conversationMessageService;

    public ConversationMessageStateUpdateListener(ConversationMessageService conversationMessageService) {
        this.conversationMessageService = conversationMessageService;
    }

    @KafkaListener(topics = {"${buzzy-api.kafka.conversation-message-state.topic}"},
            groupId = "${buzzy-api.kafka.conversation-message-state.consumer.group-id}")
    public void stateUpdate(ConsumerRecord<String, ConversationMessageUpdateDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(ConversationMessageUpdateDTO stateUpdate) {
        LOGGER.debug("Processing conversation message state update: {}", JsonUtil.toJson(stateUpdate));

        if (stateUpdate.getLastReadMessageId() != null) {
            conversationMessageService.bulkSetReadState(stateUpdate);
        } else {
            conversationMessageService.setReadState(stateUpdate);
        }
    }
}
