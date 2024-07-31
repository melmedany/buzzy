package io.buzzy.common.security.token.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static io.buzzy.common.security.Constants.CLAIMS_AUTHORITIES_KEY;
import static io.buzzy.common.security.Constants.CLAIMS_USERNAME_KEY;

public class TokenAuthenticationConverter implements OpaqueTokenAuthenticationConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationConverter.class);


    @Override
    public Authentication convert(String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        Map<String, Object> attributes = authenticatedPrincipal.getAttributes();

        Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        if (authenticatedPrincipal.getAttributes().containsKey(CLAIMS_AUTHORITIES_KEY)) {

            Collection<String> claimsAuthorities = (Collection<String>) authenticatedPrincipal.getAttributes().get(CLAIMS_AUTHORITIES_KEY);

            authorities = claimsAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        String username = null;
        if (attributes.containsKey(CLAIMS_USERNAME_KEY) && StringUtils.hasText((String) attributes.get(CLAIMS_USERNAME_KEY))) {
            username = (String) attributes.get(CLAIMS_USERNAME_KEY);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                introspectedToken, authenticatedPrincipal.getAttribute(IdTokenClaimNames.IAT),
                authenticatedPrincipal.getAttribute(IdTokenClaimNames.EXP));

        OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(username, attributes, authorities);

        return new BearerTokenAuthentication(principal, accessToken, authorities);
    }
}
