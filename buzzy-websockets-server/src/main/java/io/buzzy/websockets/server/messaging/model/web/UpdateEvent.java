package io.buzzy.websockets.server.messaging.model.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEvent<T extends UpdateEventBody> {
    private final UpdateEventType type;
    private final T body;
    private final List<String> receivers;

    private UpdateEvent(UpdateEventType type, T body, List<String> receivers) {
        this.type = type;
        this.body = body;
        this.receivers = receivers;
    }

    public UpdateEventType getType() {
        return type;
    }

    public T getBody() {
        return body;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public static UserStatusUpdateBuilder userStatusUpdate() {
        return new UserStatusUpdateBuilder();
    }

    public static ConversationMessageUpdateBuilder messageUpdate() {
        return new ConversationMessageUpdateBuilder();
    }

    public static ConversationUpdateBuilder conversationUpdate() {
        return new ConversationUpdateBuilder();
    }

    public abstract static class Builder<B extends UpdateEventBody, T extends Builder<B, T>> {
        protected B body;
        protected List<String> receivers;

        public T receivers(List<String> usernames) {
            this.receivers = usernames;
            return self();
        }

        protected abstract T self();

        public UpdateEvent<B> build(UpdateEventType type) {
            return new UpdateEvent<>(type, body, receivers);
        }
    }

    public static class UserStatusUpdateBuilder extends Builder<UserStatusUpdate, UserStatusUpdateBuilder> {
        public UserStatusUpdateBuilder() {
            this.body = new UserStatusUpdate();
        }

        public UserStatusUpdateBuilder username(String username) {
            this.body.setUsername(username);
            return self();
        }

        public UserStatusUpdateBuilder status(UserStatusUpdate.UserStatus status) {
            this.body.setStatus(status);
            return self();
        }

        @Override
        protected UserStatusUpdateBuilder self() {
            return this;
        }

        public UpdateEvent<UserStatusUpdate> build() {
            return super.build(UpdateEventType.online_offline_status);
        }
    }

    public static class ConversationMessageUpdateBuilder extends Builder<ConversationMessageUpdate, ConversationMessageUpdateBuilder> {
        public ConversationMessageUpdateBuilder() {
            this.body = new ConversationMessageUpdate();
        }

        public ConversationMessageUpdateBuilder conversationMessageUpdateDTO(ConversationMessageUpdateDTO dto) {
            this.body.setMessageId(dto.getMessageId());
            this.body.setConversationId(dto.getConversationId());
            this.body.setState(dto.getState());

            this.receivers = dto.getReceiversUsernames();

            return self();
        }


        @Override
        protected ConversationMessageUpdateBuilder self() {
            return this;
        }

        public UpdateEvent<ConversationMessageUpdate> build() {
            return super.build(UpdateEventType.message_update);
        }
    }

    public static class ConversationUpdateBuilder extends Builder<ConversationUpdate, ConversationUpdateBuilder> {
        public ConversationUpdateBuilder() {
            this.body = new ConversationUpdate();
        }

        public ConversationUpdateBuilder conversationId(String conversationId) {
            this.body.setConversationId(conversationId);
            return self();
        }

        @Override
        protected ConversationUpdateBuilder self() {
            return this;
        }

        public UpdateEvent<ConversationUpdate> build() {
            return super.build(UpdateEventType.conversation_update);
        }
    }
}
