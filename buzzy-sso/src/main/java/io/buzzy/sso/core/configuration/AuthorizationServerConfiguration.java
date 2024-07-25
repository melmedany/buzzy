package io.buzzy.sso.core.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.sso.authentication.ApiAuthenticationConverter;
import io.buzzy.sso.authentication.ApiAuthenticationFailureHandler;
import io.buzzy.sso.authentication.ApiAuthenticationProvider;
import io.buzzy.sso.authentication.ApiAuthenticationTokenMixin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

import java.util.List;

@Configuration
public class AuthorizationServerConfiguration {
    private final String issuer;


    public AuthorizationServerConfiguration(@Value("${buzzy-sso.issuer}") String issuer) {
        this.issuer = issuer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http, ApiAuthenticationProvider apiAuthenticationProvider,
    ApiAuthenticationFailureHandler apiAuthenticationFailureHandler) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(apiAuthenticationConverter())
                        .errorResponseHandler(apiAuthenticationFailureHandler)
                        .authenticationProvider(apiAuthenticationProvider));

        http.addFilterAfter(apiAuthenticationFailureHandler, ExceptionTranslationFilter.class);

        return http.build();
    }

    @Bean
    public ApiAuthenticationConverter apiAuthenticationConverter() {
        return new ApiAuthenticationConverter();
    }

    @Bean
    public ApiAuthenticationFailureHandler apiAuthenticationFailureHandler(ResourceBundleMessagesService messageSource) {
        return new ApiAuthenticationFailureHandler(messageSource);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ApiAuthenticationProvider apiAuthenticationProvider(UserDetailsService userDetailsService,
                                                               OAuth2TokenGenerator<?> jwtTokenCustomizer,
                                                               OAuth2AuthorizationService authorizationService,
                                                               PasswordEncoder passwordEncoder) {
        return new ApiAuthenticationProvider(authorizationService, jwtTokenCustomizer, userDetailsService, passwordEncoder);
    }

    @Bean
    public JdbcRegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public JdbcOAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
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
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}