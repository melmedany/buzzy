package io.buzzy.api.conversation.messaging;

import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.NewConnectionDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewConnectionAddedListenerTest {

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private ConsumerRecord<String, NewConnectionDTO> message;

    @InjectMocks
    private NewConnectionAddedListener newConnectionAddedListener;

    @Test
    public void testNewConnection_Success() {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(UUID.randomUUID());
        UserProfile connectionProfile = new UserProfile();
        connectionProfile.setId(UUID.randomUUID());

        NewConnectionDTO newConnectionDTO = new NewConnectionDTO(userProfile.getId().toString(), connectionProfile.getId().toString());

        when(message.value()).thenReturn(newConnectionDTO);
        when(userProfileService.findById(userProfile.getId().toString())).thenReturn(userProfile);
        when(userProfileService.findById(connectionProfile.getId().toString())).thenReturn(connectionProfile);

        newConnectionAddedListener.newConnection(message, acknowledgment);

        verify(userProfileService, times(1)).findById(newConnectionDTO.getUserId());
        verify(userProfileService, times(1)).findById(newConnectionDTO.getConnectionId());
        verify(conversationService, times(1)).createConversation(userProfile, connectionProfile);
        verify(acknowledgment, times(1)).acknowledge();
        verify(acknowledgment, never()).nack(any());
    }
}