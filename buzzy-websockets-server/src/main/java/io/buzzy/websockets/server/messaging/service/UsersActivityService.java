package io.buzzy.websockets.server.messaging.service;


import io.buzzy.websockets.server.messaging.model.*;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivity;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivityRepository;
import io.buzzy.websockets.server.messaging.service.exception.UserActivityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UsersActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersActivityService.class);
    private static final String USER_DESTINATION = "/user/{username}/";
    private static final String POST_MESSAGE_DESTINATION = "/conversation/{conversationId}/user/{username}/";
    private final UserActivityRepository userActivityRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public UsersActivityService(UserActivityRepository userActivityRepository, SimpMessagingTemplate messagingTemplate) {
        this.userActivityRepository = userActivityRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void online(Authentication user) {
        if (user == null) {
            return;
        }

        String username = user.getName();

        UserActivity userActivity = new UserActivity(username, true, OffsetDateTime.now());
        userActivityRepository.save(userActivity);

        LOGGER.debug("{} became online", username);

//        UpdateEvent<UserStatusUpdate> userOnline = UpdateEvent.userStatusUpdate()
//                .status(UserStatusUpdate.UserStatus.online)
//                .username(username)
//                .receivers()
//                .build();
//
//        notify(userOnline);
    }

    public void offline(Authentication user) {
        if (user == null) {
            return;
        }

        String username = user.getName();
        userActivityRepository.deleteById(username);

        LOGGER.debug("{} became offline", username);

//        UpdateEvent<UserStatusUpdate> userOnline = UpdateEvent.userStatusUpdate()
//                .status(UserStatusUpdate.UserStatus.offline)
//                .username(username)
//                .receivers()
//                .build();
//
//        notify(userOnline);
    }

    public void subscribe(BearerTokenAuthentication user, String destinationToSubscribe) {
        String username = user.getName();
        UserActivity userActivity = getUserActivity(username);

        if (userActivity.getSubscriptions().contains(destinationToSubscribe)) {
            LOGGER.debug("{} already subscribed to {}", username, destinationToSubscribe);
            return;
        }

        userActivity.getSubscriptions().add(destinationToSubscribe);
        userActivityRepository.save(userActivity);

        LOGGER.debug("{} subscribed to {}", username, destinationToSubscribe);
    }

    public void unsubscribe(BearerTokenAuthentication user, String destinationToUnsubscribe) {
        String username = user.getName();
        UserActivity userActivity = getUserActivity(username);

        if (!userActivity.getSubscriptions().contains(destinationToUnsubscribe)) {
            LOGGER.debug("{} is not subscribed to {}", username, destinationToUnsubscribe);
            return;
        }

        userActivity.getSubscriptions().remove(destinationToUnsubscribe);
        userActivityRepository.save(userActivity);

        LOGGER.debug("unsubscription! {} unsubscribed {}", username, destinationToUnsubscribe);
    }

    public void notify(UpdateEvent<? extends UpdateEventBody> updateEvent) {
        if (updateEvent == null) return;

        List<String> receivers = updateEvent.getReceivers();

        for (String receiver : receivers) {
            if (isOnline(receiver)) {
                String destination = determineDestination(receiver, updateEvent);

                if (destination != null) {
                    messagingTemplate.convertAndSend(destination, updateEvent.getBody());
                }

            } else {
                LOGGER.info("{} is offline. cannot inform the socket. will persist in database", receiver);
                // todo // handle save and notify once online
            }
        }
    }

    private boolean isOnline(String username) {
        return getUserActivity(username).isOnline();
    }

    private UserActivity getUserActivity(String username) {
        UserActivity userActivity = userActivityRepository.findById(username).orElse(null);

        if (userActivity == null) {
            throw new UserActivityNotFoundException("No user activity found for {}" + username);
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
            case post_message -> {
                return postMessageDestination(receiver, ((UpdateEvent<PostMessageUpdate>) updateEvent).getBody().getConversationId());
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

    private String postMessageDestination(String receiver, String conversationId) {
        String destination = POST_MESSAGE_DESTINATION.replace("{conversationId}}", conversationId)
                .replace("{username}", receiver);

        boolean userSubscribed = getUserActivity(receiver).getSubscriptions().contains(destination);

        if (!userSubscribed && isOnline(receiver)) {
            UpdateEvent<ConversationUpdate> event = UpdateEvent.conversationUpdate()
                    .conversationId(conversationId)
                    .receivers(List.of(receiver))
                    .build();

            notify(event);
        }

        return destination;
    }



    private String conversationUpdate(String receiver) {
        return USER_DESTINATION.replace("{username}", receiver);
    }
}
