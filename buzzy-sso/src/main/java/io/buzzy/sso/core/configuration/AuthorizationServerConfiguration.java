package io.buzzy.sso.core.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.sso.authentication.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.util.List;

@Configuration
public class AuthorizationServerConfiguration {
    private final String issuer;


    public AuthorizationServerConfiguration(@Value("${buzzy-sso.issuer}") String issuer) {
        this.issuer = issuer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http, AuthenticationProvider apiAuthenticationProvider,
                                                                ApiAuthenticationExceptionFilter apiAuthenticationExceptionFilter) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .authenticationProvider(apiAuthenticationProvider)
                        .accessTokenRequestConverter(apiAuthenticationConverter())
                        .errorResponseHandler(apiAuthenticationFailureHandler()));

        http.addFilterAfter(apiAuthenticationExceptionFilter, ExceptionTranslationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider apiAuthenticationProvider(UserDetailsService userDetailsService,
                                                            OAuth2TokenGenerator<?> tokenCustomizer,
                                                            OAuth2AuthorizationService authorizationService,
                                                            PasswordEncoder passwordEncoder) {
        return new ApiAuthenticationProvider(authorizationService, tokenCustomizer, userDetailsService, passwordEncoder);
    }

    @Bean
    public AuthenticationConverter apiAuthenticationConverter() {
        return new ApiAuthenticationConverter();
    }

    @Bean
    public AuthenticationFailureHandler apiAuthenticationFailureHandler() {
        return new ApiAuthenticationFailureHandler();
    }

    @Bean
    public ApiAuthenticationExceptionFilter apiAuthenticationFilter(ResourceBundleMessagesService messageSource) {
        return new ApiAuthenticationExceptionFilter(messageSource);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService authorizationService = new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);

        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
        JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper parametersMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper();


        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.addMixIn(OAuth2ClientAuthenticationToken.class, ApiAuthenticationTokenMixin.class);

        rowMapper.setObjectMapper(objectMapper);
        parametersMapper.setObjectMapper(objectMapper);
        authorizationService.setAuthorizationRowMapper(rowMapper);
        return authorizationService;
    }

    @Bean
    public RegisteredClientRepository jdbcRegisteredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}