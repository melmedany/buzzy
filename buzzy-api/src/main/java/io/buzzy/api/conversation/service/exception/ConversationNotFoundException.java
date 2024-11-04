package io.buzzy.api.conversation.service.exception;

public class ConversationNotFoundException extends RuntimeException {
    private final String conversationId;

    public ConversationNotFoundException(String conversationId, String errorMessage) {
        super(errorMessage);
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
