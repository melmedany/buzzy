package io.buzzy.api.profile.controller;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = UserProfileController.class)
public class UserProfileAPIExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileAPIExceptionHandler.class);

    private final ResourceBundleMessagesService messageSource;

    public UserProfileAPIExceptionHandler(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<APIResponse<Void>> handleException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("{}: ", e.getClass().getSimpleName());
        String errorMessage = messageSource.getMessage(e.getMessage(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse<>(null, List.of(new ApiError(null, errorMessage))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error("MethodArgumentNotValidException: ", e);
        List<ApiError> errors = fieldValidationError(e.getBindingResult(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse<>(null, errors), HttpStatus.BAD_REQUEST);
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