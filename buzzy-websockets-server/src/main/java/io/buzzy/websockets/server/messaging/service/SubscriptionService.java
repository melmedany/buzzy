package io.buzzy.websockets.server.messaging.service;


import io.buzzy.websockets.server.messaging.repository.entity.UserActivity;
import io.buzzy.websockets.server.messaging.repository.entity.UserActivityRepository;
import io.buzzy.websockets.server.messaging.repository.entity.UserSubscription;
import io.buzzy.websockets.server.messaging.service.exception.UserActivityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);
    private final UserActivityRepository userActivityRepository;

    public SubscriptionService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void subscribe(BearerTokenAuthentication user, UserSubscription subscription) {
        String username = user.getName();
        UserActivity userActivity = getUserActivity(username);

        boolean destinationSubscribed = userActivity.getSubscriptions().values().stream()
                .anyMatch(s -> s.getDestination().equalsIgnoreCase(subscription.getDestination()));

        if (destinationSubscribed) {
            userActivity.getSubscriptions().values().stream()
                    .filter(s -> s.getDestination().equalsIgnoreCase(subscription.getDestination()))
                    .forEach(s -> unsubscribe(user, s.getId()));

            userActivity = getUserActivity(username);
//            LOGGER.debug("{} already subscribed to {}", username, subscription.getDestination());
//            return;
        }

        userActivity.getSubscriptions().put(subscription.getId(), subscription);
        userActivityRepository.save(userActivity);

        LOGGER.debug("{} subscribed to {}", username, subscription.getDestination());
    }

    public void unsubscribe(BearerTokenAuthentication user, String sessionId) {
        String username = user.getName();
        UserActivity userActivity = getUserActivity(username);

        if (!userActivity.getSubscriptions().containsKey(sessionId)) {
            LOGGER.debug("{} has no subscription with id {}", username, sessionId);
            return;
        }

        userActivity.getSubscriptions().remove(sessionId);
        userActivityRepository.save(userActivity);

        LOGGER.debug("user {} unsubscribed from session {}", username, sessionId);
    }

    private UserActivity getUserActivity(String username) {
        UserActivity userActivity = userActivityRepository.findById(username).orElse(null);

        if (userActivity == null) {
            throw new UserActivityNotFoundException(String.format("No user activity found for {%s}", username));
        }

        return userActivity;
    }
}
