package io.buzzy.api.conversation.service;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.mapper.ConversationMapper;
import io.buzzy.api.conversation.model.ConversationType;
import io.buzzy.api.conversation.repository.ConversationRepository;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationConfiguration;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    public void testGetConversationsSummary_Success() {
        String username = "testuser";
        UUID id = UUID.randomUUID();
        List<Conversation> conversations = List.of(new Conversation());
        conversations.forEach(conversation -> conversation.setParticipants(new ArrayList<>()));
        List<ConversationDTO> conversationDTOs = List.of(new ConversationDTO());
        conversationDTOs.forEach(conversation -> conversation.setParticipants(new ArrayList<>()));

        when(userProfileService.findByUsername(username)).thenReturn(new UserProfileDTO(id, username));
        when(conversationRepository.findConversationSummaryForUser(id)).thenReturn(conversations);
        when(conversationMapper.toDTOList(conversations)).thenReturn(conversationDTOs);

        List<ConversationDTO> result = conversationService.getConversationsSummary(username);

        assertEquals(conversationDTOs, result);
        verify(conversationMapper).toDTOList(conversations);
        verify(conversationRepository).findConversationSummaryForUser(id);
    }

    @Test
    public void testGetConversation_Success() {
        String username = "testuser";
        UUID conversationId = UUID.randomUUID();
        Conversation conversation = new Conversation();
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        conversation.setParticipants(List.of(userProfile));
        ConversationDTO conversationDTO = new ConversationDTO();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationMapper.toDTO(conversation)).thenReturn(conversationDTO);

        ConversationDTO result = conversationService.getConversation(conversationId.toString(), username);

        assertEquals(conversationDTO, result);
        verify(conversationRepository).findById(conversationId);
        verify(conversationMapper).toDTO(conversation);
    }

    @Test
    public void testGetConversation_NotFound() {
        String username = "testuser";
        UUID conversationId = UUID.randomUUID();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        assertThrows(ConversationNotFoundException.class, () ->
                conversationService.getConversation(conversationId.toString(), username));
    }

    @Test
    public void testGetConversation_UserNotInConversation() {
        String username = "testuser";
        UUID conversationId = UUID.randomUUID();
        Conversation conversation = new Conversation();
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername("otheruser");
        conversation.setParticipants(List.of(userProfile));

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        assertThrows(ConversationNotFoundException.class, () ->
                conversationService.getConversation(conversationId.toString(), username));
    }

    @Test
    public void testCreateConversation_NewProfile() {
        UserProfile newProfile = new UserProfile();
        ConversationConfiguration configuration = new ConversationConfiguration();
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.direct_message);
        conversation.setConfiguration(configuration);
        conversation.setParticipants(List.of(newProfile));
        configuration.setConversation(conversation);

        conversationService.createConversation(newProfile);

        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    public void testCreateConversation_WithUserAndConnection() {
        UserProfile user = new UserProfile();
        UserProfile connection = new UserProfile();
        ConversationConfiguration configuration = new ConversationConfiguration();
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.direct_message);
        conversation.setConfiguration(configuration);
        conversation.setParticipants(List.of(user, connection));
        configuration.setConversation(conversation);

        conversationService.createConversation(user, connection);

        verify(conversationRepository).save(any(Conversation.class));
    }
}