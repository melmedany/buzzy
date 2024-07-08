package io.buzzy.websockets.server.messaging.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Service
public class WebSocketSessionEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSessionEventListener.class);

    private static final String SIMP_SESSION_ID_KEY = "simpSessionId";
    private static final String SIMP_DESTINATION_KEY = "simpDestination";

    private final UsersActivityService usersActivityService;

    public WebSocketSessionEventListener(UsersActivityService usersActivityService) {
        this.usersActivityService = usersActivityService;
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

        usersActivityService.subscribe((BearerTokenAuthentication) event.getUser(), destinationToSubscribe);
    }

    @EventListener
    public void sessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String session = (String) event.getMessage().getHeaders().get(SIMP_SESSION_ID_KEY);
        String destinationToUnsubscribe = (String) event.getMessage().getHeaders().get(SIMP_DESTINATION_KEY);

        if (destinationToUnsubscribe == null || event.getUser() == null) {
            LOGGER.debug("Ignoring session unsubscribe event because no destination specified, session: {}, destination: {}", session, destinationToUnsubscribe);
            return;
        }

        usersActivityService.unsubscribe((BearerTokenAuthentication) event.getUser(), destinationToUnsubscribe);
    }
}
