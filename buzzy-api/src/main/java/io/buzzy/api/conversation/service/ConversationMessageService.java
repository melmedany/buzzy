package io.buzzy.api.conversation.service;

import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.api.conversation.mapper.ConversationMessageMapper;
import io.buzzy.api.conversation.messaging.PostMessageUpdateProducer;
import io.buzzy.api.conversation.model.ConversationMessageState;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.conversation.repository.entity.ConversationMessageRepository;
import io.buzzy.api.conversation.repository.entity.ConversationRepository;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.conversation.service.exception.UserCannotPostMessageException;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConversationMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageService.class);

    private final UserProfileService userProfileService;
    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final ConversationMessageMapper conversationMessageMapper;
    private final PostMessageUpdateProducer postMessageUpdateProducer;

    public ConversationMessageService(ConversationRepository conversationRepository,
                                      UserProfileService userProfileService,
                                      ConversationMessageRepository conversationMessageRepository,
                                      ConversationMessageMapper conversationMessageMapper,
                                      PostMessageUpdateProducer postMessageUpdateProducer) {
        this.conversationRepository = conversationRepository;
        this.userProfileService = userProfileService;
        this.conversationMessageRepository = conversationMessageRepository;
        this.conversationMessageMapper = conversationMessageMapper;
        this.postMessageUpdateProducer = postMessageUpdateProducer;
    }

    public void postMessage(String username, String conversationId, PostMessageRequest postMessage) {
        Conversation conversation = conversationRepository.findById(UUID.fromString(conversationId))
                .orElseThrow(() -> new ConversationNotFoundException(conversationId, "Conversation not found!"));

        verifySender(username, conversation);

        UserProfileDTO user = userProfileService.findByUsername(username);

        // TODO // sanitize message

        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setType(postMessage.getType());
        conversationMessage.setState(ConversationMessageState.created);
        conversationMessage.setSender(user.getId());
        conversationMessage.setText(postMessage.getMessage());

        conversationMessage.setConversation(conversation);

        conversationMessageRepository.save(conversationMessage);
        conversation.getMessages().add(conversationMessage);
        conversationRepository.save(conversation);

        postMessageUpdateProducer.send(conversationMessageMapper.toPostMessageUpdateDTO(username, conversation, conversationMessage));
    }

    private void verifySender(String username, Conversation conversation) {
        boolean loggedInUserInConversationParticipants =
                conversation.getParticipants().stream().map(UserProfile::getUsername)
                        .anyMatch(participant -> participant.equalsIgnoreCase(username));

        if (!loggedInUserInConversationParticipants) {
            throw new UserCannotPostMessageException(username, conversation.getId().toString(), "global.user.is.not.participating.conversation");
        }
    }
}
