package io.buzzy.websockets.server.messaging.service;

import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import io.buzzy.common.util.JsonUtil;
import io.buzzy.websockets.server.messaging.model.web.ConversationMessageUpdate;
import io.buzzy.websockets.server.messaging.model.web.UpdateEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConversationMessageUpdateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageUpdateListener.class);

    private final NotificationService notificationService;

    public ConversationMessageUpdateListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = {"${buzzy-websockets-server.kafka.message-update.topic}"},
            groupId = "${buzzy-websockets-server.kafka.message-update.group-id}")
    public void conversationMessageUpdate(ConsumerRecord<String, ConversationMessageUpdateDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(ConversationMessageUpdateDTO conversationMessageUpdateDTO) {
        LOGGER.debug("Processing message update: {}", JsonUtil.toJson(conversationMessageUpdateDTO));

        UpdateEvent<ConversationMessageUpdate> event = UpdateEvent.messageUpdate()
                .conversationMessageUpdateDTO(conversationMessageUpdateDTO)
                .build();

        notificationService.notify(event);
    }
}
