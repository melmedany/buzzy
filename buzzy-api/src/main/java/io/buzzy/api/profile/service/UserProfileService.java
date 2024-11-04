package io.buzzy.api.profile.service;

import io.buzzy.api.profile.controller.model.SearchProfileDTO;
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

    private final NewConnectionProducer newConnectionProducer;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final SettingsMapper settingsMapper;

    public UserProfileService(NewConnectionProducer newConnectionProducer,
                              UserProfileRepository userProfileRepository,
                              UserProfileMapper userProfileMapper,
                              SettingsMapper settingsMapper) {
        this.newConnectionProducer = newConnectionProducer;
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
        return findByIdInternal(id);
    }

    public UserProfile findById(String id) {
        return findUserProfileByIdInternal(id);
    }

    public UserProfileDTO findByUsername(String username) {
        return findByUsernameInternal(username);
    }

    public UserProfile findUserProfileByUsername(String username) {
        return findUserProfileByUsernameInternal(username);
    }

    public void updateUserProfile(String username, UserProfileDTO updatedProfileDTO) {
        UserProfile userProfile = findUserProfileByUsernameInternal(username);

        userProfileMapper.update(userProfile, updatedProfileDTO);

        userProfileRepository.save(userProfile);
    }

    public List<SearchProfileDTO> searchUserProfiles(String keyword, String loggedInUsername) {
        return userProfileMapper.toSearchProfileDTOList(userProfileRepository.searchUserProfiles(keyword, loggedInUsername));
    }

    public void addConnection(String username, String idToConnect) {
        UserProfile userProfile = findUserProfileByUsernameInternal(username);

        if (userProfile.getId().toString().equalsIgnoreCase(idToConnect)) {
            throw new IllegalArgumentException("global.user.cannot.self.connect");
        }

        UserProfile connection = findUserProfileByIdInternal(idToConnect);

        boolean connectionExists = userProfile.getConnections().stream()
                .anyMatch(profile -> profile.getId().toString().equalsIgnoreCase(idToConnect));

        if (connectionExists) {
            throw new ConnectionAlreadyExistException(userProfile.getId(), connection.getId(), "global.user.already.connected");
        }

        userProfile.getConnections().add(connection);
        connection.getConnections().add(userProfile);

        userProfileRepository.save(userProfile);

        newConnectionProducer.send(userProfileMapper.toNewConnectionDTO(userProfile.getId(), connection.getId()));
    }

    public void updateSettings(String username, SettingsDTO settingsDTO) {
        UserProfile userProfile = findUserProfileByUsernameInternal(username);

        userProfile.setSettings(settingsMapper.toEntity(settingsDTO));
        userProfileRepository.save(userProfile);
    }

    public void userStatusUpdate(UserStatusUpdateDTO userStatusUpdateDTO) {
        UserProfile userProfile = findUserProfileByUsernameInternal(userStatusUpdateDTO.getUsername());

        userProfile.setLastSeen(userStatusUpdateDTO.getLastSeen());
        userProfileRepository.save(userProfile);
    }

    private UserProfileDTO findByUsernameInternal(String username) {
        UserProfile userProfile = findUserProfileByUsernameInternal(username);
        return userProfileMapper.toUserProfileDTO(userProfile);
    }

    private UserProfile findUserProfileByUsernameInternal(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfile;
    }

    private UserProfileDTO findByIdInternal(UUID id) {
        UserProfile userProfile = findUserProfileByIdInternal(id.toString());
        return userProfileMapper.toUserProfileDTO(userProfile);
    }

    private UserProfile findUserProfileByIdInternal(String id) {
        UserProfile userProfile = userProfileRepository.findById(UUID.fromString(id)).orElse(null);

        if (userProfile == null) {
            throw new UsernameNotFoundException("global.user.not.found");
        }

        return userProfile;
    }
}
