package io.buzzy.api.conversation.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.buzzy.api.conversation.model.ConversationMessageState;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationMessageDTO {
    private String id;
    private String type;
    @JsonProperty("content")
    private String text;
    private OffsetDateTime date;
    private String sender;
    private ConversationMessageState state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ConversationMessageState getState() {
        return state;
    }

    public void setState(ConversationMessageState state) {
        this.state = state;
    }
}
