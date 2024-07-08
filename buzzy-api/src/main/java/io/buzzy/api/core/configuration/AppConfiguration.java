package io.buzzy.api.core.configuration;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.LocaleUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Configuration
public class AppConfiguration {
    @Bean
    public ResourceBundleMessagesService resourceBundleMessagesService(MessageSource messageSource) {
        return new ResourceBundleMessagesService(messageSource);
    }

    @Bean
    public LocaleResolver localizationResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                return LocaleUtil.getRequestLocale(request);
            }
        };
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(getMessageBundleLocations());
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    private String[] getMessageBundleLocations() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath:messages/*.properties");
            return Arrays.stream(resources).map(this::resourceToMessageBundle).filter(Objects::nonNull).toArray(String[]::new);
        } catch (Exception e) {
            return new String[]{};
        }
    }

    private String resourceToMessageBundle(Resource resource) {
        String filename = resource.getFilename();
        int lastUnderscoreIndex = filename.lastIndexOf('_');
        if (lastUnderscoreIndex != -1) {
            return "classpath:messages/" + filename.substring(0, lastUnderscoreIndex);
        }
        return null;
    }
}
