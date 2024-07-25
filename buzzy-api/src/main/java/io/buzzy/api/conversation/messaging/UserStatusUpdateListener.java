package io.buzzy.api.conversation.messaging;

import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.UserStatusUpdateDTO;
import io.buzzy.common.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserStatusUpdateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatusUpdateListener.class);

    private final UserProfileService userProfileService;

    public UserStatusUpdateListener(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @KafkaListener(topics = {"${buzzy-api.kafka.user-status-update.topic}"},
            groupId = "${buzzy-api.kafka.user-status-update.consumer.group-id}")
    public void lastSeenUpdate(ConsumerRecord<String, UserStatusUpdateDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(UserStatusUpdateDTO userStatusUpdateDTO) {
        LOGGER.debug("Processing last seen update: {}", JsonUtil.toJson(userStatusUpdateDTO));
        userProfileService.userStatusUpdate(userStatusUpdateDTO);
    }
}
