package io.buzzy.api.profile;

import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
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
public class SuccessfulRegistrationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuccessfulRegistrationListener.class);

    private final ConversationService conversationService;
    private final UserProfileService userProfileService;

    public SuccessfulRegistrationListener(ConversationService conversationService, UserProfileService userProfileService) {
        this.conversationService = conversationService;
        this.userProfileService = userProfileService;
    }

    @Transactional
    @KafkaListener(topics = {"${buzzy-api.kafka.successful-registration.topic}"},
            groupId = "${buzzy-api.kafka.successful-registration.consumer.group-id}")
    public void successfulRegistration(ConsumerRecord<String, SuccessfulRegistrationDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(SuccessfulRegistrationDTO successfulRegistrationDTO) {
        LOGGER.debug("Processing successful registration: {}", JsonUtil.toJson(successfulRegistrationDTO));
        UserProfile newProfile = userProfileService.createNewProfile(successfulRegistrationDTO);
        conversationService.createConversation(newProfile);
    }
}
