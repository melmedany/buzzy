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
import io.buzzy.common.messaging.model.UserStatusUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileService.class);

    private final NewConnectionAddedProducer newConnectionAddedProducer;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final SettingsMapper settingsMapper;

    public UserProfileService(NewConnectionAddedProducer newConnectionAddedProducer,
                              UserProfileRepository userProfileRepository,
                              UserProfileMapper userProfileMapper,
                              SettingsMapper settingsMapper) {
        this.newConnectionAddedProducer = newConnectionAddedProducer;
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.settingsMapper = settingsMapper;
    }

    public UserProfile createNewProfile(SuccessfulRegistrationDTO successfulRegistrationDTO) {
        UserProfile userProfile = userProfileMapper.successfulRegistrationToUserProfile(successfulRegistrationDTO);
        userProfile.setActive(true);
        userProfile.setSettings(new Settings());
        userProfile.setLastSeen(OffsetDateTime.now());
        userProfile.setCreated(OffsetDateTime.now());
        userProfile.setUpdated(OffsetDateTime.now());
        return userProfileRepository.saveAndFlush(userProfile);
    }

    public UserProfileDTO findById(UUID id) {
        UserProfile userProfile = userProfileRepository.findByUserId(id).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfileMapper.toUserProfileDTO(userProfile);
    }

    public UserProfile findById(String id) {
        UserProfile userProfile = userProfileRepository.findById(UUID.fromString(id)).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfile;
    }

    public UserProfileDTO findByUsername(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfileMapper.toUserProfileDTO(userProfile);
    }

    public UserProfile findByUserProfileUsername(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfile;
    }

    public List<UserProfileDTO> searchUserProfiles(String keyword, String loggedInUsername) {
        return userProfileMapper.toUserProfileDTOList(userProfileRepository.searchUserProfiles(keyword, loggedInUsername));
    }

    public void addConnection(String username, String connectionId) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        UserProfile connection = userProfileRepository.findById(UUID.fromString(connectionId)).orElse(null);

        if (connection == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        if (userProfile.getId() == connection.getId()) {
            throw new IllegalArgumentException("global.user.cannot.self.connect");
        }

        boolean connectionExists = userProfile.getConnections().stream()
                .anyMatch(profile -> profile.getId().toString().equals(connectionId));

        if (connectionExists) {
            throw new ConnectionAlreadyExistException(username, connection.getUsername(), "global.user.already.connected");
        }

        userProfile.getConnections().add(connection);
        connection.getConnections().add(userProfile);

        userProfileRepository.save(userProfile);

        newConnectionAddedProducer.send(userProfileMapper.toNewConnectionDTO(userProfile.getId(), connection.getId()));
    }

    public void updateSettings(String username, SettingsDTO settingsDTO) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        userProfile.setSettings(settingsMapper.toEntity(settingsDTO));
        userProfileRepository.save(userProfile);
    }

    public void userStatusUpdate(UserStatusUpdateDTO userStatusUpdateDTO) {
        UserProfile userProfile = userProfileRepository.findByUsername(userStatusUpdateDTO.getUsername()).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        userProfile.setLastSeen(userStatusUpdateDTO.getLastSeen());
        userProfileRepository.save(userProfile);
    }
}
