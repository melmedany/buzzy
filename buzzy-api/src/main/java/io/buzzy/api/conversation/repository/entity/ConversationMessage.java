package io.buzzy.api.conversation.repository.entity;

import io.buzzy.api.conversation.model.ConversationMessageState;
import io.buzzy.api.conversation.model.ConversationMessageType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation_messages")
public class ConversationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private ConversationMessageType type;
    @Enumerated(EnumType.STRING)
    private ConversationMessageState state;
    private UUID sender;
    private String text;
    @CreationTimestamp
    private OffsetDateTime created;
    @UpdateTimestamp
    private OffsetDateTime updated;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ConversationMessageType getType() {
        return type;
    }

    public void setType(ConversationMessageType type) {
        this.type = type;
    }

    public ConversationMessageState getState() {
        return state;
    }

    public void setState(ConversationMessageState state) {
        this.state = state;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
