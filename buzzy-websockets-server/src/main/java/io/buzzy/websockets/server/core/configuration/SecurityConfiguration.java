package io.buzzy.websockets.server.core.configuration;

import io.buzzy.common.security.token.converter.TokenAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
public class SecurityConfiguration {
    private final String introspectionUri;
    private final String clientId;
    private final String clientSecret;

    public SecurityConfiguration(@Value("${buzzy-websockets-server.oauth2.opaquetoken.introspection-uri}") String introspectionUri,
                                 @Value("${buzzy-websockets-server.oauth2.resources.server.client-id}") String clientId,
                                 @Value("${buzzy-websockets-server.oauth2.resources.server.client-secret}") String clientSecret) {
        this.introspectionUri = introspectionUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer.opaqueToken(opaqueTokenConfigurer ->
                        opaqueTokenConfigurer.introspectionUri(introspectionUri)
                                .introspectionClientCredentials(clientId, clientSecret)
                                .authenticationConverter(tokenAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public OpaqueTokenAuthenticationConverter tokenAuthenticationConverter() {
        return new TokenAuthenticationConverter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
