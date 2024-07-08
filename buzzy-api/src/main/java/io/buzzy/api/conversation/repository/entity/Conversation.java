package io.buzzy.api.conversation.repository.entity;

import io.buzzy.api.conversation.model.ConversationType;
import io.buzzy.api.profile.repository.entity.UserProfile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private ConversationType type;
    @CreationTimestamp
    private OffsetDateTime created;
    @UpdateTimestamp
    private OffsetDateTime updated;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_conversations",
            joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"))
    private List <UserProfile> participants;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private Collection<ConversationMessage> messages;

    @OneToOne(mappedBy = "conversation", cascade = CascadeType.ALL)
    private ConversationConfiguration configuration;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
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

    public List<UserProfile> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserProfile> participants) {
        this.participants = participants;
    }

    public Collection<ConversationMessage> getMessages() {
        return messages;
    }

    public void setMessages(Collection<ConversationMessage> messages) {
        this.messages = messages;
    }

    public ConversationConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConversationConfiguration configuration) {
        this.configuration = configuration;
    }
}
