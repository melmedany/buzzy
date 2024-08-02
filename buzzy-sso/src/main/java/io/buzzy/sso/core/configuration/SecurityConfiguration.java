package io.buzzy.sso.core.configuration;

import io.buzzy.sso.authentication.ApiAuthenticationExceptionFilter;
import io.buzzy.sso.authentication.service.TokenIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, ApiAuthenticationExceptionFilter apiAuthenticationExceptionFilter,
                                                          OpaqueTokenIntrospector tokenIntrospector) throws Exception {
        return http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/static").permitAll()
                        .requestMatchers("/v1/**").hasAuthority("write")
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer.introspector(tokenIntrospector)))
                .addFilterAfter(apiAuthenticationExceptionFilter, ExceptionTranslationFilter.class)
                .build();
    }

    @Bean
    public OpaqueTokenIntrospector tokenIntrospector(OAuth2AuthorizationService authorizationService) {
        return new TokenIntrospector(authorizationService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}