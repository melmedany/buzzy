package io.buzzy.sso.registration.controller;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.ApiExceptionHandler;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import io.buzzy.common.web.model.ApiErrorCode;
import io.buzzy.sso.registration.service.exception.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = RegistrationController.class)
public class RegistrationAPIExceptionHandler extends ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationAPIExceptionHandler.class);

    public RegistrationAPIExceptionHandler(ResourceBundleMessagesService messageSource) {
        super(messageSource);
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class})
    public ResponseEntity<APIResponse<Void>> handleException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("{}: ", e.getClass().getSimpleName());

        String errorMessage = messageSource.getMessage(e.getMessage(), LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.UsernameAlreadyExists);

        return new ResponseEntity<>(new APIResponse<>(null, List.of(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error("MethodArgumentNotValidException: ", e);
        List<ApiError> errors = fieldValidationError(e.getBindingResult(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse<>(null, errors), HttpStatus.BAD_REQUEST);
    }
}