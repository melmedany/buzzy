package io.buzzy.sso.core;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ResourceBundleMessagesService messageSource;

    public GlobalExceptionHandler(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        LOGGER.error("An unexpected error: ", e);

        String errorMessage = messageSource.getMessage("global.error", LocaleUtil.getRequestLocale(request));

        return new ResponseEntity<>(new APIResponse<>(null, List.of(new ApiError(null, errorMessage))), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}