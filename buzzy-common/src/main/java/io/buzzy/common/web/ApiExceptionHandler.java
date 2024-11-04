package io.buzzy.common.web;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.web.model.ApiError;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

public abstract class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    protected final ResourceBundleMessagesService messageSource;

    protected ApiExceptionHandler(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }


    protected List<ApiError> fieldValidationError(BindingResult bindingResult, Locale locale) {
        List<ApiError> errors = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, Object> fieldValidationArguments = fieldValidationArguments(error, locale);
            ApiError apiError = new ApiError(error.getField(), messageSource.getMessage(error.getDefaultMessage(), fieldValidationArguments, locale));
            errors.add(apiError);
        }
        return errors;
    }

    protected Map<String, Object> fieldValidationArguments(FieldError error, Locale locale) {
        ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
        Map<String, Object> arguments = new HashMap<>(violation.getConstraintDescriptor().getAttributes());
        arguments.put("field", messageSource.getMessage("validation.field." + error.getField(), locale));
        return arguments;
    }
}
