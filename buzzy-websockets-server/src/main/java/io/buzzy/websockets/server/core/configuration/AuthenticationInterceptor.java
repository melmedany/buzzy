package io.buzzy.websockets.server.core.configuration;

import io.buzzy.websockets.server.messaging.service.TokenValidationService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthenticationInterceptor implements ChannelInterceptor {

    private final TokenValidationService tokenValidationService;
    private final OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter;

    public AuthenticationInterceptor(TokenValidationService tokenValidationService, OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter) {
        this.tokenValidationService = tokenValidationService;
        this.opaqueTokenAuthenticationConverter = opaqueTokenAuthenticationConverter;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                OAuth2AuthenticatedPrincipal principal = tokenValidationService.validateToken(token);
                if (principal != null) {
                    BearerTokenAuthentication authentication = (BearerTokenAuthentication) opaqueTokenAuthenticationConverter.convert(token, principal);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                }
            }
        }
        return message;
    }

}