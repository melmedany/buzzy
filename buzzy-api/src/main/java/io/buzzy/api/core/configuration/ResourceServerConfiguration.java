package io.buzzy.api.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.buzzy.common.security.Constants.CLAIMS_AUTHORITIES_KEY;
import static io.buzzy.common.security.Constants.CLAIMS_USERNAME_KEY;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class ResourceServerConfiguration {
    private final String introspectionUri;
    private final String clientId;
    private final String clientSecret;

    public ResourceServerConfiguration(@Value("${buzzy-api.oauth2.opaquetoken.introspection-uri}") String introspectionUri,
                                       @Value("${buzzy-api.oauth2.resources.server.client-id}") String clientId,
                                       @Value("${buzzy-api.oauth2.resources.server.client-secret}") String clientSecret) {
        this.introspectionUri = introspectionUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/v1/**").hasAuthority("USER")
                        .anyRequest().permitAll());

        http.oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer.opaqueToken(opaqueTokenConfigurer ->
                        opaqueTokenConfigurer.introspectionUri(introspectionUri)
                                .introspectionClientCredentials(clientId, clientSecret)
                                .authenticationConverter(opaqueTokenAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter() {
        return (introspectedToken, authenticatedPrincipal) -> {
            Map<String, Object> attributes = authenticatedPrincipal.getAttributes();

            Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
            if (authenticatedPrincipal.getAttributes().containsKey(CLAIMS_AUTHORITIES_KEY)) {
                authorities =
                        ((List<String>) authenticatedPrincipal.getAttributes().get(CLAIMS_AUTHORITIES_KEY)).stream()
                                .map(auth -> new SimpleGrantedAuthority(auth))
                                .collect(Collectors.toUnmodifiableSet());
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
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
