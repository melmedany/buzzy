package io.buzzy.api.profile.mapper;

import io.buzzy.api.profile.controller.model.UserConnectionDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.common.messaging.model.NewConnectionDTO;
import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    @Mapping(target = "connections", ignore = true)
    @Mapping(target = "settings", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    UserProfile successfulRegistrationToUserProfile(SuccessfulRegistrationDTO successfulRegistrationDTO);

    UserProfileDTO toUserProfileDTO(UserProfile userProfile);

    List<UserProfileDTO> toUserProfileDTOList(List<UserProfile> userProfile);

    default NewConnectionDTO toNewConnectionDTO(UUID userId, UUID connectionId) {
        NewConnectionDTO newConnectionDTO = new NewConnectionDTO();
        newConnectionDTO.setConnectionId(connectionId.toString());
        newConnectionDTO.setUserId(userId.toString());
        return newConnectionDTO;
    }

    default UUID mapToUUID(String userId) {
        return UUID.fromString(userId);
    }

    default UserConnectionDTO mapToUserConnectionDTO(UserProfile connection) {
        UserConnectionDTO connectionDTO = new UserConnectionDTO();
        connectionDTO.setId(connection.getId());
        connectionDTO.setUsername(connection.getUsername());
        connectionDTO.setFirstname(connection.getFirstname());
        connectionDTO.setLastname(connection.getLastname());
        connectionDTO.setLastSeen(connection.getLastSeen());

        return connectionDTO;
    }
}
