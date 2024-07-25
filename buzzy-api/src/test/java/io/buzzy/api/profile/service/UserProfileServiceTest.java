package io.buzzy.api.profile.service;

import io.buzzy.api.profile.controller.model.SettingsDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.mapper.SettingsMapper;
import io.buzzy.api.profile.mapper.UserProfileMapper;
import io.buzzy.api.profile.repository.entity.Settings;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.repository.entity.UserProfileRepository;
import io.buzzy.api.profile.service.exception.ConnectionAlreadyExistException;
import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private NewConnectionAddedProducer newConnectionAddedProducer;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private SettingsMapper settingsMapper;

    @InjectMocks
    private UserProfileService userProfileService;

    private SuccessfulRegistrationDTO successfulRegistrationDTO;
    private UserProfile userProfile;
    private UserProfileDTO userProfileDTO;
    private SettingsDTO settingsDTO;

    @BeforeEach
    void setUp() {
        successfulRegistrationDTO = new SuccessfulRegistrationDTO();
        userProfile = new UserProfile();
        userProfileDTO = new UserProfileDTO();
        settingsDTO = new SettingsDTO();

        userProfile.setId(UUID.randomUUID());
        userProfile.setUsername("testuser");
        userProfile.setActive(true);
        userProfile.setSettings(new Settings());
    }

    @Test
    void createNewProfile() {
        when(userProfileMapper.successfulRegistrationToUserProfile(successfulRegistrationDTO)).thenReturn(userProfile);
        when(userProfileRepository.saveAndFlush(any(UserProfile.class))).thenReturn(userProfile);

        UserProfile result = userProfileService.createNewProfile(successfulRegistrationDTO);

        assertNotNull(result);
        assertTrue(result.isActive());
        assertNotNull(result.getCreated());
        assertNotNull(result.getUpdated());
        verify(userProfileRepository, times(1)).saveAndFlush(userProfile);
    }

    @Test
    void findById_existingId() {
        UUID id = userProfile.getId();
        when(userProfileRepository.findByUserId(id)).thenReturn(Optional.of(userProfile));
        when(userProfileMapper.toUserProfileDTO(userProfile)).thenReturn(userProfileDTO);

        UserProfileDTO result = userProfileService.findById(id);

        assertNotNull(result);
        verify(userProfileRepository, times(1)).findByUserId(id);
    }

    @Test
    void findById_nonExistingId() {
        UUID id = UUID.randomUUID();
        when(userProfileRepository.findByUserId(id)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.findById(id));
    }

    @Test
    void findByUsername_existingUsername() {
        String username = userProfile.getUsername();
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(userProfileMapper.toUserProfileDTO(userProfile)).thenReturn(userProfileDTO);

        UserProfileDTO result = userProfileService.findByUsername(username);

        assertNotNull(result);
        verify(userProfileRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsername_nonExistingUsername() {
        String username = "nonexistinguser";
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.findByUsername(username));
    }

    @Test
    void searchUserProfiles() {
        String keyword = "test";
        String loggedInUsername = "loggedUser";
        List<UserProfile> userProfiles = Collections.singletonList(userProfile);
        List<UserProfileDTO> userProfileDTOs = Collections.singletonList(userProfileDTO);

        when(userProfileRepository.searchUserProfiles(keyword, loggedInUsername)).thenReturn(userProfiles);
        when(userProfileMapper.toUserProfileDTOList(userProfiles)).thenReturn(userProfileDTOs);

        List<UserProfileDTO> result = userProfileService.searchUserProfiles(keyword, loggedInUsername);

        assertEquals(1, result.size());
        assertEquals(userProfileDTO, result.get(0));
        verify(userProfileRepository, times(1)).searchUserProfiles(keyword, loggedInUsername);
    }

    @Test
    void searchUserProfiles_emptyResults() {
        String keyword = "test";
        String loggedInUsername = "loggedUser";

        when(userProfileRepository.searchUserProfiles(keyword, loggedInUsername)).thenReturn(Collections.emptyList());
        when(userProfileMapper.toUserProfileDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<UserProfileDTO> result = userProfileService.searchUserProfiles(keyword, loggedInUsername);

        assertTrue(result.isEmpty());
        verify(userProfileRepository, times(1)).searchUserProfiles(keyword, loggedInUsername);
    }

    @Test
    void searchUserProfiles_nullKeyword() {
        String loggedInUsername = "loggedUser";

        when(userProfileRepository.searchUserProfiles(null, loggedInUsername)).thenReturn(Collections.emptyList());
        when(userProfileMapper.toUserProfileDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<UserProfileDTO> result = userProfileService.searchUserProfiles(null, loggedInUsername);

        assertTrue(result.isEmpty());
        verify(userProfileRepository, times(1)).searchUserProfiles(null, loggedInUsername);
    }

    @Test
    void addConnection_successful() {
        String username = userProfile.getUsername();
        String connectionId = UUID.randomUUID().toString();
        UserProfile connectionProfile = new UserProfile();
        connectionProfile.setId(UUID.fromString(connectionId));
        connectionProfile.setUsername("connectionUser");

        userProfile.setConnections(new ArrayList<>());
        connectionProfile.setConnections(new ArrayList<>());

        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.findById(UUID.fromString(connectionId))).thenReturn(Optional.of(connectionProfile));

        userProfileService.addConnection(username, connectionId);

        assertTrue(userProfile.getConnections().contains(connectionProfile));
        assertTrue(connectionProfile.getConnections().contains(userProfile));
        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, times(1)).findById(UUID.fromString(connectionId));
        verify(userProfileRepository, times(1)).save(userProfile);
        verify(newConnectionAddedProducer, times(1)).send(any());
    }

    @Test
    void addConnection_userNotFound() {
        String username = "nonexistinguser";
        String connectionId = UUID.randomUUID().toString();

        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.addConnection(username, connectionId));

        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, never()).findById(any());
        verify(userProfileRepository, never()).save(any());
        verify(newConnectionAddedProducer, never()).send(any());
    }

    @Test
    void addConnection_connectionNotFound() {
        String username = userProfile.getUsername();
        String connectionId = UUID.randomUUID().toString();

        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.findById(UUID.fromString(connectionId))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.addConnection(username, connectionId));

        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, times(1)).findById(UUID.fromString(connectionId));
        verify(userProfileRepository, never()).save(any());
        verify(newConnectionAddedProducer, never()).send(any());
    }

    @Test
    void addConnection_connectionAlreadyExists() {
        String username = userProfile.getUsername();
        String connectionId = UUID.randomUUID().toString();
        UserProfile connectionProfile = new UserProfile();
        connectionProfile.setId(UUID.fromString(connectionId));
        connectionProfile.setUsername("connectionUser");

        userProfile.setConnections(List.of(connectionProfile));

        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.findById(UUID.fromString(connectionId))).thenReturn(Optional.of(connectionProfile));

        assertThrows(ConnectionAlreadyExistException.class, () -> userProfileService.addConnection(username, connectionId));

        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, times(1)).findById(UUID.fromString(connectionId));
        verify(userProfileRepository, never()).save(any());
        verify(newConnectionAddedProducer, never()).send(any());
    }

    @Test
    void addConnection_selfConnection() {
        String username = userProfile.getUsername();
        String connectionId = userProfile.getId().toString();

        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.findById(UUID.fromString(connectionId))).thenReturn(Optional.of(userProfile));

        assertThrows(IllegalArgumentException.class, () -> userProfileService.addConnection(username, connectionId));

        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, times(1)).findById(UUID.fromString(connectionId));
        verify(userProfileRepository, never()).save(any());
        verify(newConnectionAddedProducer, never()).send(any());
    }

    @Test
    void updateSettings() {
        String username = userProfile.getUsername();
        Settings settings = new Settings();
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));
        when(settingsMapper.toEntity(settingsDTO)).thenReturn(settings);

        userProfileService.updateSettings(username, settingsDTO);

        assertEquals(settings, userProfile.getSettings());
        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    void updateSettings_userNotFound() {
        String username = "nonexistinguser";
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.updateSettings(username, settingsDTO));

        verify(userProfileRepository, times(1)).findByUsername(username);
        verify(userProfileRepository, never()).save(any());
    }
}
