package io.buzzy.api.conversation.service;

import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.api.conversation.mapper.ConversationMessageMapper;
import io.buzzy.api.conversation.messaging.ConversationMessageUpdateProducer;
import io.buzzy.api.conversation.model.ConversationMessageState;
import io.buzzy.api.conversation.model.ConversationMessageType;
import io.buzzy.api.conversation.repository.ConversationMessageRepository;
import io.buzzy.api.conversation.repository.ConversationRepository;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.conversation.service.exception.ConversationMessageNotFoundException;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.conversation.service.exception.UserCannotPostMessageException;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageService.class);

    private final UserProfileService userProfileService;
    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final ConversationMessageMapper conversationMessageMapper;
    private final ConversationMessageUpdateProducer conversationMessageUpdateProducer;

    public ConversationMessageService(ConversationRepository conversationRepository,
                                      UserProfileService userProfileService,
                                      ConversationMessageRepository conversationMessageRepository,
                                      ConversationMessageMapper conversationMessageMapper,
                                      ConversationMessageUpdateProducer conversationMessageUpdateProducer) {
        this.conversationRepository = conversationRepository;
        this.userProfileService = userProfileService;
        this.conversationMessageRepository = conversationMessageRepository;
        this.conversationMessageMapper = conversationMessageMapper;
        this.conversationMessageUpdateProducer = conversationMessageUpdateProducer;
    }

    public void postMessage(String username, String conversationId, PostMessageRequest postMessage) {
        Conversation conversation = conversationRepository.findById(UUID.fromString(conversationId))
                .orElseThrow(() -> new ConversationNotFoundException(conversationId, "global.conversations.not.found"));

        verifyUserInConversation(username, conversation);

        UserProfile sender = userProfileService.findUserProfileByUsername(username);

        // TODO // sanitize message

        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setType(ConversationMessageType.text);
        conversationMessage.setState(ConversationMessageState.sending);
        conversationMessage.setSender(sender);
        conversationMessage.setText(postMessage.getMessage());

        conversationMessage.setConversation(conversation);

        conversationMessageRepository.save(conversationMessage);
        conversation.getMessages().add(conversationMessage);
        conversationRepository.save(conversation);

        conversationMessageUpdateProducer.send(conversationMessageMapper
                .toConversationMessageUpdateDTO(conversation, conversationMessage, false));
    }

    public ConversationMessageDTO getConversationMessage(String messageId, String conversationId, String username) {
        ConversationMessage conversationMessage = conversationMessageRepository.findByIdAndConversationId(UUID.fromString(messageId),
                        UUID.fromString(conversationId)).orElse(null);

        if (conversationMessage == null) {
            throw new ConversationMessageNotFoundException(messageId, conversationId, "global.conversation.message.not.found");
        }

        boolean userInConversationParticipants = conversationMessage.getConversation().getParticipants().stream()
                .anyMatch(participant -> username.equalsIgnoreCase(participant.getUsername()));

        if (!userInConversationParticipants) {
            throw new ConversationNotFoundException(conversationId, "global.conversations.not.found");
        }

        return conversationMessageMapper.toDTO(conversationMessage);
    }

    @Transactional
    public void setReadState(ConversationMessageUpdateDTO conversationMessageUpdate) {
        Conversation conversation = getConversation(conversationMessageUpdate.getConversationId());

        ConversationMessage conversationMessage = conversationMessageRepository
                .findByIdAndConversationId(UUID.fromString(conversationMessageUpdate.getMessageId()),
                        UUID.fromString(conversationMessageUpdate.getConversationId())).orElse(null);

        if (conversationMessage == null) {
            throw new ConversationMessageNotFoundException(conversationMessageUpdate.getMessageId(),
                    conversationMessageUpdate.getConversationId(), "Conversation Message not found!");
        }

        verifyUserInConversation(conversationMessageUpdate.getUpdateInitiator(), conversation);

        conversationMessage.setState(ConversationMessageState.read);
        conversationMessageRepository.save(conversationMessage);

        conversationMessageUpdateProducer.send(conversationMessageMapper
                .toConversationMessageUpdateDTO(conversation, conversationMessage, true));
    }

    @Transactional
    public void bulkSetReadState(ConversationMessageUpdateDTO conversationMessageUpdate) {
        Conversation conversation = getConversation(conversationMessageUpdate.getConversationId());

        verifyUserInConversation(conversationMessageUpdate.getUpdateInitiator(), conversation);

        List<ConversationMessage> toUpdate = conversationMessageRepository.findForBulkUpdateState(
                UUID.fromString(conversationMessageUpdate.getLastReadMessageId()),
                conversation.getId(),
                ConversationMessageState.read);

        if (!toUpdate.isEmpty()) {
            int updated = conversationMessageRepository.bulkUpdateState(
                    UUID.fromString(conversationMessageUpdate.getLastReadMessageId()),
                    conversation.getId(),
                    ConversationMessageState.read);

            LOGGER.debug("Bulk updated {} conversation message state", updated);

            toUpdate.forEach(conversationMessage -> conversationMessageUpdateProducer.send(conversationMessageMapper
                    .toConversationMessageUpdateDTO(conversation, conversationMessage, true)));
        }
    }

    private Conversation getConversation(String conversationId) {
        Conversation conversation = conversationRepository.findById(UUID.fromString(conversationId)).orElse(null);

        if (conversation == null) {
            throw new ConversationNotFoundException(conversationId, "global.conversations.not.found");
        }

        return conversation;
    }

    private void verifyUserInConversation(String username, Conversation conversation) {
        boolean userInConversationParticipants =
                conversation.getParticipants().stream().map(UserProfile::getUsername)
                        .anyMatch(participant -> participant.equalsIgnoreCase(username));

        if (!userInConversationParticipants) {
            throw new UserCannotPostMessageException(username, conversation.getId().toString(),
                    "global.user.is.not.participating.conversation");
        }
    }
}
