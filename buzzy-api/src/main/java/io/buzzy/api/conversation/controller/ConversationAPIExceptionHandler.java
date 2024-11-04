package io.buzzy.api.conversation.controller;

import io.buzzy.api.conversation.service.exception.ConversationMessageNotFoundException;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.conversation.service.exception.UserCannotPostMessageException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ConversationController.class)
public class ConversationAPIExceptionHandler extends ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationAPIExceptionHandler.class);

    public ConversationAPIExceptionHandler(ResourceBundleMessagesService messageSource) {
        super(messageSource);
    }

    @ExceptionHandler({UserCannotPostMessageException.class})
    public ResponseEntity<APIResponse<Void>> handleException(UserCannotPostMessageException e, HttpServletRequest request) {
        LOGGER.error("{}: ", e.getClass().getSimpleName());
        String errorMessage = messageSource.getMessage(e.getMessage(), LocaleUtil.getRequestLocale(request));
        return new ResponseEntity<>(new APIResponse<>(null, List.of(new ApiError(null, errorMessage))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleConversationNotFoundException(ConversationNotFoundException e, HttpServletRequest request) {
        LOGGER.error("ConversationNotFoundException: ", e);
        Map<String, Object> arguments = Map.of("conversationId", e.getConversationId());
        String errorMessage = messageSource.getMessage(e.getMessage(), arguments, LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.ConversationNotFound);
        return new ResponseEntity<>(new APIResponse<>(null, List.of(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversationMessageNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleConversationNotFoundException(ConversationMessageNotFoundException e, HttpServletRequest request) {
        LOGGER.error("ConversationMessageNotFoundException: ", e);
        Map<String, Object> arguments = Map.of("messageId", e.getMessageId(),"conversationId", e.getConversationId());
        String errorMessage = messageSource.getMessage(e.getMessage(), arguments, LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.ConversationMessageNotFound);
        return new ResponseEntity<>(new APIResponse<>(null, List.of(error)), HttpStatus.BAD_REQUEST);
    }
}