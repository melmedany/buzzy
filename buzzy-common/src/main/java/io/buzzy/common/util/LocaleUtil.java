package io.buzzy.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Locale;

public class LocaleUtil {
    private static final List<Locale> LOCALES = List.of(Locale.ENGLISH, Locale.of("nl"));

    private LocaleUtil() {
        // no instantiation
    }

    public static Locale getRequestLocale(HttpServletRequest request) {
        if (request == null || StringUtils.isBlank(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
            return getDefaultLocale();
        }

        String acceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        return StringUtils.isBlank(acceptLanguage) ? getDefaultLocale() : lookupLocale(acceptLanguage);
    }

    private static Locale lookupLocale(String locale) {
        return Locale.lookup(Locale.LanguageRange.parse(locale), LOCALES);
    }

    private static Locale getDefaultLocale() {
        return Locale.getDefault();
    }
}
