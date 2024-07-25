package io.buzzy.api.conversation.service.exception;

public class ConversationMessageNotFoundException extends RuntimeException {
    private final String messageId;
    private final String conversationId;

    public ConversationMessageNotFoundException(String messageId, String conversationId, String errorMessage) {
        super(errorMessage);
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

}
