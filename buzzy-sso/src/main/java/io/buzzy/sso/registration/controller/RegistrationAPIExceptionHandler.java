package io.buzzy.sso.registration.controller;

import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import io.buzzy.sso.core.service.ResourceBundleMessagesService;
import io.buzzy.sso.registration.service.exception.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = RegistrationController.class)
public class RegistrationAPIExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationAPIExceptionHandler.class);

    private final ResourceBundleMessagesService messageSource;

    public RegistrationAPIExceptionHandler(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class})
    public ResponseEntity<APIResponse> handleException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("{}: ", e.getClass().getSimpleName());
        String errorMessage = messageSource.getMessage(e.getMessage(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse(null, List.of(new ApiError(null, errorMessage))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error("MethodArgumentNotValidException: ", e);
        List<ApiError> errors = fieldValidationError(e.getBindingResult(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse(null, errors), HttpStatus.BAD_REQUEST);
    }


    private List<ApiError> fieldValidationError(BindingResult bindingResult, Locale locale) {
        List<ApiError> errors = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, Object> fieldValidationArguments = fieldValidationArguments(error, locale);
            ApiError apiError = new ApiError(error.getField(), messageSource.getMessage(error.getDefaultMessage(), fieldValidationArguments, locale));
            errors.add(apiError);
        }
        return errors;
    }

    private Map<String, Object> fieldValidationArguments(FieldError error, Locale locale) {
        ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
        Map<String, Object> arguments = new HashMap<>(violation.getConstraintDescriptor().getAttributes());
        arguments.put("field", messageSource.getMessage("validation.field." + error.getField(), locale));
        return arguments;
    }
}