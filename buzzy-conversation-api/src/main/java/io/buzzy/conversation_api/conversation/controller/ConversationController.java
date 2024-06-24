package io.buzzy.conversation_api.conversation.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class ConversationController {

    @GetMapping("/conversation")
    ResponseEntity<String> conversation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        return new ResponseEntity<>(String.format("Starting new conversation for %s.", principalName), HttpStatus.OK);
    }
}
