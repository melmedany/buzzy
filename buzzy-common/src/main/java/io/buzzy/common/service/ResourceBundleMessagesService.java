package io.buzzy.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;
import java.util.Map;

public class ResourceBundleMessagesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBundleMessagesService.class);

    private final MessageSource messageSource;

    public ResourceBundleMessagesService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Locale locale) {
        return getMessage(key, Map.of(), locale);
    }

    public String getMessage(String key, Map<String, Object> args, Locale locale) {
        String message = null;
        try {
            message = messageSource.getMessage(key, null, locale);
            message = format(message, args);
        } catch (NoSuchMessageException e) {
            LOGGER.error("Couldn't find message for key '{}'", key, e);
        }
        return message;
    }

    private String format(String message, Map<String, Object> args) {
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return message;
    }
}
