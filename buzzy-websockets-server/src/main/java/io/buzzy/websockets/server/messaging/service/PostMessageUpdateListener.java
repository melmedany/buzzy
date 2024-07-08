package io.buzzy.websockets.server.messaging.service;

import io.buzzy.common.messaging.model.PostMessageUpdateDTO;
import io.buzzy.common.util.JsonUtil;
import io.buzzy.websockets.server.messaging.model.PostMessageUpdate;
import io.buzzy.websockets.server.messaging.model.UpdateEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PostMessageUpdateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostMessageUpdateListener.class);

    private final UsersActivityService usersActivityService;

    public PostMessageUpdateListener(UsersActivityService usersActivityService) {
        this.usersActivityService = usersActivityService;
    }

    @KafkaListener(topics = {"${buzzy-websockets-server.kafka.post-message-update-topic}"},
            groupId = "${buzzy-websockets-server.kafka.post-message-update.group-id}")
    public void postMessageUpdate(ConsumerRecord<String, PostMessageUpdateDTO> message, Acknowledgment acknowledgment) {
        LOGGER.debug("Received message on topic: {} value: {}", message.topic(), JsonUtil.toJson(message.value()));

        try {
            process(message.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing message: {}", JsonUtil.toJson(message.value()), e);
            acknowledgment.nack(Duration.ofMinutes(5));
        }
    }

    protected void process(PostMessageUpdateDTO postMessageUpdateDTO) {
        LOGGER.debug("Processing post message update: {}", JsonUtil.toJson(postMessageUpdateDTO));

        UpdateEvent<PostMessageUpdate> event = UpdateEvent.postMessageUpdate()
                .postMessageUpdateDTO(postMessageUpdateDTO)
                .build();

        usersActivityService.notify(event);
    }
}
