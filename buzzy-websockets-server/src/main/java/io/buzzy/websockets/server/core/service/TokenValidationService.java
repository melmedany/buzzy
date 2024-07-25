package io.buzzy.websockets.server.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.stereotype.Service;

@Service
public class TokenValidationService {
    private final OpaqueTokenIntrospector tokenIntrospector;

    public TokenValidationService(@Value("${buzzy-websockets-server.oauth2.opaquetoken.introspection-uri}") String introspectionUri,
                                  @Value("${buzzy-websockets-server.oauth2.resources.server.client-id}") String clientId,
                                  @Value("${buzzy-websockets-server.oauth2.resources.server.client-secret}") String clientSecret) {
        this.tokenIntrospector = new SpringOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
    }

    public OAuth2AuthenticatedPrincipal validateToken(String token) {
        try {
            return tokenIntrospector.introspect(token);
        } catch (Exception e) {
            return null;
        }
    }
}