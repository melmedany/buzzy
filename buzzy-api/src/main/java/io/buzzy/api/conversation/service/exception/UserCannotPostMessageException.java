package io.buzzy.api.conversation.service.exception;

public class UserCannotPostMessageException extends RuntimeException {
    private final String username;
    private final String conversationId;

    public UserCannotPostMessageException(String username, String conversationId, String errorMessage) {
        super(errorMessage);
        this.username = username;
        this.conversationId = conversationId;
    }

}
