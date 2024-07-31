package io.buzzy.api.conversation.service;

import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.api.conversation.mapper.ConversationMessageMapper;
import io.buzzy.api.conversation.messaging.ConversationMessageUpdateProducer;
import io.buzzy.api.conversation.model.ConversationMessageType;
import io.buzzy.api.conversation.repository.ConversationMessageRepository;
import io.buzzy.api.conversation.repository.ConversationRepository;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.conversation.service.exception.UserCannotPostMessageException;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversationMessageServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMessageRepository conversationMessageRepository;

    @Mock
    private ConversationMessageMapper conversationMessageMapper;

    @Mock
    private ConversationMessageUpdateProducer conversationMessageUpdateProducer;

    @InjectMocks
    private ConversationMessageService conversationMessageService;

    @Test
    public void testPostMessage_Success() {
        String username = "testuser";
        String conversationId = UUID.randomUUID().toString();
        PostMessageRequest postMessage = new PostMessageRequest();
        postMessage.setType(ConversationMessageType.text);
        postMessage.setMessage("Long text message");

        Conversation conversation = new Conversation();
        conversation.setId(UUID.fromString(conversationId));
        conversation.setMessages(new ArrayList<>());

        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        conversation.setParticipants(List.of(userProfile));

        when(conversationRepository.findById(UUID.fromString(conversationId))).thenReturn(Optional.of(conversation));
        when(conversationMessageMapper.toConversationMessageUpdateDTO(eq(conversation), any(ConversationMessage.class), eq(false)))
                .thenReturn(new ConversationMessageUpdateDTO());

        conversationMessageService.postMessage(username, conversationId, postMessage);

        verify(conversationRepository).findById(UUID.fromString(conversationId));
        verify(conversationMessageRepository).save(any(ConversationMessage.class));
        verify(conversationRepository, times(1)).save(conversation);
        verify(conversationMessageUpdateProducer).send(any(ConversationMessageUpdateDTO.class));
    }

    @Test
    public void testPostMessage_ConversationNotFound() {
        String username = "testuser";
        String conversationId = UUID.randomUUID().toString();
        PostMessageRequest postMessage = new PostMessageRequest();

        when(conversationRepository.findById(UUID.fromString(conversationId))).thenReturn(Optional.empty());

        assertThrows(ConversationNotFoundException.class, () ->
                conversationMessageService.postMessage(username, conversationId, postMessage));
    }

    @Test
    public void testPostMessage_UserNotInConversation() {
        String username = "testuser";
        String conversationId = UUID.randomUUID().toString();
        PostMessageRequest postMessage = new PostMessageRequest();

        Conversation conversation = new Conversation();
        conversation.setId(UUID.fromString(conversationId));
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername("otheruser");
        conversation.setParticipants(List.of(userProfile));

        when(conversationRepository.findById(UUID.fromString(conversationId))).thenReturn(Optional.of(conversation));

        assertThrows(UserCannotPostMessageException.class, () ->
                conversationMessageService.postMessage(username, conversationId, postMessage));
    }
}