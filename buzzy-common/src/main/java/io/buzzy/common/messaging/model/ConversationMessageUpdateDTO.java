package io.buzzy.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationMessageUpdateDTO implements MessageDTO {
    private String messageId;
    private String conversationId;
    private String state;
    private String updateInitiator;
    private List<String> receiversUsernames;
    private String lastReadMessageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUpdateInitiator() {
        return updateInitiator;
    }

    public void setUpdateInitiator(String updateInitiator) {
        this.updateInitiator = updateInitiator;
    }

    public List<String> getReceiversUsernames() {
        return receiversUsernames;
    }

    public void setReceiversUsernames(List<String> receiversUsernames) {
        this.receiversUsernames = receiversUsernames;
    }

    public String getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(String lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
