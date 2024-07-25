package io.buzzy.websockets.server.core.service;

import io.buzzy.websockets.server.messaging.repository.entity.UserSubscription;
import io.buzzy.websockets.server.messaging.service.SubscriptionService;
import io.buzzy.websockets.server.messaging.service.UsersActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.*;

@Service
public class WebSocketSessionEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSessionEventListener.class);

    private static final String SIMP_SESSION_ID_KEY = "simpSessionId";
    private static final String SIMP_DESTINATION_KEY = "simpDestination";

    private final SubscriptionService subscriptionService;
    private final UsersActivityService usersActivityService;

    public WebSocketSessionEventListener(SubscriptionService subscriptionService,
                                         UsersActivityService usersActivityService) {
        this.subscriptionService = subscriptionService;
        this.usersActivityService = usersActivityService;
    }

    @EventListener
    public void sessionConnectEvent(SessionConnectEvent event) {
        LOGGER.debug("session connect event");
//        usersActivityService.online((BearerTokenAuthentication) event.getUser());
    }

    @EventListener
    public void sessionConnectedEvent(SessionConnectedEvent event) {
        usersActivityService.online((BearerTokenAuthentication) event.getUser());
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) {
        usersActivityService.offline((BearerTokenAuthentication) event.getUser());
    }

    @EventListener
    public void sessionSubscribeEvent(SessionSubscribeEvent event) {
        String destinationToSubscribe = (String) event.getMessage().getHeaders().get(SIMP_DESTINATION_KEY);
        String session = (String) event.getMessage().getHeaders().get(SIMP_SESSION_ID_KEY);

        if (destinationToSubscribe == null || event.getUser() == null) {
            LOGGER.debug("Ignoring session subscribe event because no destination specified, session: {}, destination: {}", session, destinationToSubscribe);
            return;
        }

        subscriptionService.subscribe((BearerTokenAuthentication) event.getUser(),
                new UserSubscription(session, destinationToSubscribe));
    }

    @EventListener
    public void sessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String session = (String) event.getMessage().getHeaders().get(SIMP_SESSION_ID_KEY);

        if (session == null || event.getUser() == null) {
            LOGGER.debug("Ignoring session unsubscribe event because unknown user or session not found specified, session: {}, user: {}", session, event.getUser());
            return;
        }

        subscriptionService.unsubscribe((BearerTokenAuthentication) event.getUser(), session);
    }
}
