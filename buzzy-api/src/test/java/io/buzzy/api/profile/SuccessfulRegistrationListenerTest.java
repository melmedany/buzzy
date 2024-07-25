package io.buzzy.api.profile;

import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SuccessfulRegistrationListenerTest {

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private ConsumerRecord<String, SuccessfulRegistrationDTO> message;

    @InjectMocks
    private SuccessfulRegistrationListener successfulRegistrationListener;

    @Captor
    private ArgumentCaptor<SuccessfulRegistrationDTO> successfulRegistrationDTOCaptor;


    @Test
    public void testSuccessfulRegistration_Success() {
        SuccessfulRegistrationDTO registrationDTO = new SuccessfulRegistrationDTO();
        UserProfile userProfile = new UserProfile();
        when(message.value()).thenReturn(registrationDTO);
        when(userProfileService.createNewProfile(any(SuccessfulRegistrationDTO.class))).thenReturn(userProfile);

        successfulRegistrationListener.successfulRegistration(message, acknowledgment);

        verify(userProfileService, times(1)).createNewProfile(successfulRegistrationDTOCaptor.capture());
        verify(conversationService, times(1)).createConversation(userProfile);
        verify(acknowledgment, times(1)).acknowledge();
        verify(acknowledgment, never()).nack(any());
    }

    @Test
    public void testSuccessfulRegistration_ExceptionDuringProcessing() {
        SuccessfulRegistrationDTO registrationDTO = new SuccessfulRegistrationDTO();
        when(message.value()).thenReturn(registrationDTO);
        when(userProfileService.createNewProfile(any(SuccessfulRegistrationDTO.class))).thenThrow(new RuntimeException("Processing error"));

        successfulRegistrationListener.successfulRegistration(message, acknowledgment);

        verify(userProfileService, times(1)).createNewProfile(successfulRegistrationDTOCaptor.capture());
        verify(conversationService, never()).createConversation(any(UserProfile.class));
        verify(acknowledgment, never()).acknowledge();
        verify(acknowledgment, times(1)).nack(Duration.ofMinutes(5));
    }
}
