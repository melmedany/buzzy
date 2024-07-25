package io.buzzy.websockets.server.messaging.controller;

import io.buzzy.common.web.model.APIResponse;
import io.buzzy.websockets.server.messaging.service.ConversationMessageUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class ConversationMessageUpdateController implements ConversationMessageAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageUpdateController.class);

    private final ConversationMessageUpdateService conversationMessageUpdateService;

    public ConversationMessageUpdateController(ConversationMessageUpdateService conversationMessageUpdateService) {
        this.conversationMessageUpdateService = conversationMessageUpdateService;
    }

    @Override
    public ResponseEntity<APIResponse<Void>> setReadState(String conversationId, String messageId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        conversationMessageUpdateService.setReadState(conversationId, messageId, principalName);

        return new ResponseEntity<>(APIResponse.emptyResponse(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<APIResponse<Void>> bulkSetReadState(String conversationId, String messageId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        conversationMessageUpdateService.bulkSetReadState(conversationId, messageId, principalName);

        return new ResponseEntity<>(APIResponse.emptyResponse(), HttpStatus.CREATED);
    }
}
