package io.buzzy.sso.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static io.buzzy.common.security.Constants.CLAIMS_AUTHORITIES_KEY;
import static io.buzzy.common.security.Constants.CLAIMS_USERNAME_KEY;

@Configuration
public class TokenConfiguration {

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer) {
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();

        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, refreshTokenGenerator);
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            UserDetails userDetails = extractUserDetails(context.getPrincipal());
            validateUsername(userDetails);

            context.getClaims()
                    .claim(CLAIMS_AUTHORITIES_KEY, getAuthorities(userDetails))
                    .claim(CLAIMS_USERNAME_KEY, userDetails.getUsername());
        };
    }

    private UserDetails extractUserDetails(Object principal) {
        if (principal instanceof OAuth2ClientAuthenticationToken token) {
            return token.getDetails() instanceof UserDetails userDetails ? userDetails : registeredClientDetails(token);
        } else if (principal instanceof AbstractAuthenticationToken token) {
            return (UserDetails) token.getPrincipal();
        } else {
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass().getName());
        }
    }

    private void validateUsername(UserDetails userDetails) {
        if (!StringUtils.hasText(userDetails.getUsername())) {
            throw new IllegalStateException("Invalid UserDetails: username is empty");
        }
    }

    private Set<String> getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private UserDetails registeredClientDetails(OAuth2ClientAuthenticationToken token) {
        if (token.getPrincipal() instanceof String && token.isAuthenticated()) {
            RegisteredClient registeredClient = token.getRegisteredClient();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(registeredClient.getClientId())
                    .password(registeredClient.getClientSecret())
                    .authorities(registeredClient.getScopes().toArray(String[]::new))
                    .build();
        }
        return null;
    }
}
