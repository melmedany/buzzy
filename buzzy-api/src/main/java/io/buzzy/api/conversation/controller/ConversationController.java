package io.buzzy.api.conversation.controller;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.api.conversation.service.ConversationMessageService;
import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.common.web.model.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class ConversationController implements ConversationAPI{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;
    private final ConversationMessageService conversationMessageService;

    public ConversationController(ConversationService conversationService, ConversationMessageService conversationMessageService) {
        this.conversationService = conversationService;
        this.conversationMessageService = conversationMessageService;
    }

    @Override
    public ResponseEntity<APIResponse<List<ConversationDTO>>> getConversationsSummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<ConversationDTO> summary = conversationService.getConversationsSummary(username);

        return new ResponseEntity<>(new APIResponse<>(summary, null), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIResponse<ConversationDTO>> getConversation(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ConversationDTO conversationDTO = conversationService.getConversation(id, username);

        return new ResponseEntity<>(new APIResponse<>(conversationDTO, null), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIResponse<ConversationDTO>> postMessage(String conversationId, PostMessageRequest postMessageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        conversationMessageService.postMessage(username, conversationId, postMessageRequest);
        return new ResponseEntity<>(APIResponse.emptyResponse(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<APIResponse<ConversationMessageDTO>> getConversationMessage(String messageId, String conversationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ConversationMessageDTO conversationMessageDTO = conversationMessageService.getConversationMessage(messageId, conversationId, username);

        return new ResponseEntity<>(new APIResponse<>(conversationMessageDTO, null), HttpStatus.OK);
    }

}
