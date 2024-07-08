package io.buzzy.common.util;

import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DateFormatUtil {
    private static final String DEFAULT_FORMATTER_KEY = "DEFAULT_FORMATTER";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final Map<String, DateTimeFormatter> FORMATTERS_CACHE = new HashMap<>(){{
        put(DEFAULT_FORMATTER_KEY, DEFAULT_FORMATTER);
    }};

    public static String format(OffsetDateTime dateTime, String pattern) {
        return format(dateTime, getInstance(pattern));
    }

    private static String format(OffsetDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }

        return dateTime.format(formatter);
    }

    private static DateTimeFormatter getInstance() {
        return getInstance(DEFAULT_FORMATTER_KEY);
    }

    private static DateTimeFormatter getInstance(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            throw new IllegalArgumentException("The format pattern parameter cannot be null");
        }

        if (FORMATTERS_CACHE.isEmpty() || !FORMATTERS_CACHE.containsKey(pattern)) {
            try {
                FORMATTERS_CACHE.put(pattern, DateTimeFormatter.ofPattern(pattern));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid date format pattern: " + pattern, e);
            }
        }

        return FORMATTERS_CACHE.get(pattern);
    }
}
