package io.buzzy.api.profile.controller;

import io.buzzy.api.profile.service.exception.ConnectionAlreadyExistException;
import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.ApiExceptionHandler;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import io.buzzy.common.web.model.ApiErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = UserProfileController.class)
public class UserProfileAPIExceptionHandler extends ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileAPIExceptionHandler.class);

    public UserProfileAPIExceptionHandler(ResourceBundleMessagesService messageSource) {
        super(messageSource);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<APIResponse<Void>> handleException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("{}: ", e.getClass().getSimpleName());
        String errorMessage = messageSource.getMessage(e.getMessage(), LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.ProfileNotFound);
        return new ResponseEntity<>(new APIResponse<>(null, List.of(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error("MethodArgumentNotValidException: ", e);
        List<ApiError> errors = fieldValidationError(e.getBindingResult(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse<>(null, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectionAlreadyExistException.class)
    public ResponseEntity<APIResponse<Void>> handleConnectionAlreadyExistException(ConnectionAlreadyExistException e, HttpServletRequest request) {
        LOGGER.error("ConnectionAlreadyExistException: ", e);
        Map<String, Object> arguments = Map.of("username", e.getUsername(), "connection", e.getConnection());
        String errorMessage = messageSource.getMessage(e.getMessage(), arguments, LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.ConnectionAlreadyExists);
        return new ResponseEntity<>(new APIResponse<>(null, List.of(error)), HttpStatus.BAD_REQUEST);
    }
}