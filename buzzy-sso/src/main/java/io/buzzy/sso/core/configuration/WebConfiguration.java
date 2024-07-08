package io.buzzy.sso.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebConfiguration implements WebMvcConfigurer {
    private final List<String> allowedOrigins;

    public WebConfiguration(@Value("${buzzy-sso.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedMethods = Arrays.stream(HttpMethod.values()).map(HttpMethod::name).toArray(String[]::new);

        registry.addMapping("/v1/**").allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods(allowedMethods);
        registry.addMapping("/oauth2/**").allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods(allowedMethods);
    }
}