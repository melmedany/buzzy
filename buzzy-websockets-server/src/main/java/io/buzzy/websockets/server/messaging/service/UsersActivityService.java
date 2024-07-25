package io.buzzy.websockets.server.messaging.service;


import io.buzzy.common.messaging.model.UserStatusUpdateDTO;
import io.buzzy.websockets.server.messaging.model.web.UpdateEvent;
import io.buzzy.websockets.server.messaging.model.web.UserStatusUpdate;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivity;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivityRepository;
import io.buzzy.websockets.server.messaging.service.exception.UserActivityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UsersActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersActivityService.class);
    private final UserActivityRepository userActivityRepository;
    private final NotificationService notificationService;
    private final UserStatusUpdateProducer userStatusUpdateProducer;

    public UsersActivityService(UserActivityRepository userActivityRepository,
                                NotificationService notificationService,
                                UserStatusUpdateProducer userStatusUpdateProducer) {
        this.userActivityRepository = userActivityRepository;
        this.notificationService = notificationService;
        this.userStatusUpdateProducer = userStatusUpdateProducer;
    }

    public void online(Authentication user) {
        if (user == null) {
            return;
        }

        String username = user.getName();

        UserActivity userActivity = userActivityRepository.findById(username).orElse(null);

        if (userActivity == null) {
            userActivity = new UserActivity(username, true, OffsetDateTime.now());
        } else {
            userActivity.setOnline(true);
            userActivity.setLastSeen(OffsetDateTime.now());
        }

        userActivityRepository.save(userActivity);

        LOGGER.debug("{} became online", username);
        userStatusUpdateProducer.send(new UserStatusUpdateDTO(userActivity.getUsername(), userActivity.getLastSeen()));

        UpdateEvent<UserStatusUpdate> userOnline = UpdateEvent.userStatusUpdate()
                .status(UserStatusUpdate.UserStatus.online)
                .username(username)
                .receivers(List.of()) // todo // fixme
                .build();

        notificationService.notify(userOnline);
    }

    public void offline(Authentication user) {
        if (user == null) {
            return;
        }

        String username = user.getName();
        UserActivity userActivity = getUserActivity(username);
        userActivity.setOnline(false);

        userActivityRepository.save(userActivity);

        LOGGER.debug("{} became offline", username);

        UpdateEvent<UserStatusUpdate> userOffline = UpdateEvent.userStatusUpdate()
                .status(UserStatusUpdate.UserStatus.offline)
                .username(username)
                .receivers(List.of()) // todo // fixme
                .build();

        notificationService.notify(userOffline);
    }

    private UserActivity getUserActivity(String username) {
        UserActivity userActivity = userActivityRepository.findById(username).orElse(null);

        if (userActivity == null) {
            throw new UserActivityNotFoundException(String.format("No user activity found for {%s}", username));
        }

        return userActivity;
    }
}
