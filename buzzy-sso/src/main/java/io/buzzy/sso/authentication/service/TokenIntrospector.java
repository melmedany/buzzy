package io.buzzy.sso.authentication.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static io.buzzy.common.security.Constants.CLAIMS_AUTHORITIES_KEY;

public class TokenIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;

    public TokenIntrospector(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization == null || authorization.getAccessToken() == null || authorization.getAccessToken().isExpired()) {
            throw new OAuth2IntrospectionException("Invalid token.");
        }


        Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        Map<String, Object> claims = authorization.getAccessToken().getClaims();

        Map<String, Object> attributes = authorization.getAccessToken().getClaims();

        if (claims != null && claims.containsKey(CLAIMS_AUTHORITIES_KEY)) {

            Collection<String> claimsAuthorities = (Collection<String>) claims.get(CLAIMS_AUTHORITIES_KEY);

            authorities = claimsAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return new DefaultOAuth2AuthenticatedPrincipal(authorization.getPrincipalName(), attributes, authorities);
    }
}
