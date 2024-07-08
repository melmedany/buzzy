package io.buzzy.api.conversation.controller.model;

import io.buzzy.api.conversation.model.ConversationMessageType;

public class PostMessageRequest {
    private ConversationMessageType type;
    private String message;

    public ConversationMessageType getType() {
        return type;
    }

    public void setType(ConversationMessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
