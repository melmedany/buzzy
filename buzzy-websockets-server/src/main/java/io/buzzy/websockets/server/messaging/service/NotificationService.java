package io.buzzy.websockets.server.messaging.service;


import io.buzzy.websockets.server.messaging.model.web.*;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivity;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivityRepository;
import io.buzzy.websockets.server.messaging.service.exception.UserActivityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private static final String USER_DESTINATION = "/users/{username}/";
    private static final String CONVERSATION_DESTINATION = "/conversations/{conversationId}/user/{username}/";
    private final UserActivityRepository userActivityRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(UserActivityRepository userActivityRepository, SimpMessagingTemplate messagingTemplate) {
        this.userActivityRepository = userActivityRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void notify(UpdateEvent<? extends UpdateEventBody> updateEvent) {
        if (updateEvent == null) return;

        List<String> receivers = updateEvent.getReceivers();

        for (String receiver : receivers) {
            if (isOnline(receiver)) {
                String destination = determineDestination(receiver, updateEvent);

                boolean userSubscribed = getUserActivity(receiver).getSubscriptions().values().stream().anyMatch(s -> s.getDestination().equalsIgnoreCase(destination));

                if (!userSubscribed) {
                    LOGGER.debug("User {} not subscribed to conversation {}", receiver, destination);
                    notifyUser(updateEvent, receiver);
                } else if (destination != null) {
                    LOGGER.debug("Notifying user activity of {} to {}", receiver, destination);
                    messagingTemplate.convertAndSend(destination, updateEvent.getBody());
                }

            } else {
                LOGGER.debug("{} is offline. cannot inform the socket.", receiver);
                // todo // handle save and notify once online
            }
        }
    }

    private void notifyUser(UpdateEvent<? extends UpdateEventBody> updateEvent, String receiver) {
        if (updateEvent.getBody() instanceof ConversationUpdate) {
            LOGGER.debug("Notifying conversation update activity of {} to {}", receiver, updateEvent.getBody());
            ConversationUpdate conversationUpdate = ((UpdateEvent<ConversationUpdate>) updateEvent).getBody();
            UpdateEvent<ConversationUpdate> event = UpdateEvent.conversationUpdate()
                    .conversationId(conversationUpdate.getConversationId())
                    .receivers(List.of(receiver))
                    .build();

            notify(event);
        } else if (updateEvent.getBody() instanceof ConversationMessageUpdate) {
            LOGGER.debug("Notifying conversation message update activity of {} to {}", receiver, updateEvent.getBody());
            ConversationMessageUpdate conversationMessageUpdate = ((UpdateEvent<ConversationMessageUpdate>) updateEvent).getBody();
            UpdateEvent<ConversationUpdate> event = UpdateEvent.conversationUpdate()
                    .conversationId(conversationMessageUpdate.getConversationId())
                    .receivers(List.of(receiver))
                    .build();

            notify(event);
        } else {
            throw new IllegalArgumentException("Unexpected update event type: " + updateEvent.getBody().getClass());
        }
    }

    private boolean isOnline(String username) {
        return getUserActivity(username).isOnline();
    }

    private UserActivity getUserActivity(String username) {
        UserActivity userActivity = userActivityRepository.findById(username).orElse(null);

        if (userActivity == null) {
            throw new UserActivityNotFoundException(String.format("No user activity found for {%s}", username));
        }

        return userActivity;
    }

    private String determineDestination(String receiver, UpdateEvent<? extends UpdateEventBody> updateEvent) {
        UpdateEventType type = updateEvent.getType();

        switch (type) {
            case online_offline_status -> {
                LOGGER.debug("Not yet implemented!");
                return null;
            }
            case message_update -> {
                ConversationMessageUpdate conversationMessageUpdate = ((UpdateEvent<ConversationMessageUpdate>) updateEvent).getBody();
                return messageDestination(conversationMessageUpdate.getConversationId(), receiver);
            }
            case conversation_update -> {
                return conversationUpdate(receiver);
            }
            default -> {
                LOGGER.debug("default getDestination");
                return null;
            }
        }
    }

    private String messageDestination(String conversationId, String receiver) {
        return CONVERSATION_DESTINATION
                .replace("{conversationId}", conversationId)
                .replace("{username}", receiver);
    }

    private String conversationUpdate(String receiver) {
        return USER_DESTINATION.replace("{username}", receiver);
    }
}
