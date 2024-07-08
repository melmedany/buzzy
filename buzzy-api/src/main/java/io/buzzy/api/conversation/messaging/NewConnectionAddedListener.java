package io.buzzy.api.conversation.messaging;

import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.NewConnectionDTO;
import io.buzzy.common.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class NewConnectionAddedListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewConnectionAddedListener.class);

    private final ConversationService conversationService;
    private final UserProfileService userProfileService;

    public NewConnectionAddedListener(ConversationService conversationService, UserProfileService userProfileService) {
        this.conversationService = conversationService;
        this.userProfileService = userProfileService;
    }

    @Transactional
    @KafkaListener(topics = {"${buzzy-api.kafka.new-connection-topic}"},
            groupId = "${buzzy-api.kafka.new-connection.consumer.group-id}")
    public void newConnection(ConsumerRecord<String, NewConnectionDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(NewConnectionDTO newConnectionDTO) {
        LOGGER.debug("Processing new added connection: {}", JsonUtil.toJson(newConnectionDTO));
        UserProfile user = userProfileService.findById(newConnectionDTO.getUserId());
        UserProfile connection = userProfileService.findById(newConnectionDTO.getConnectionId());
        conversationService.createConversation(user, connection);
    }
}
