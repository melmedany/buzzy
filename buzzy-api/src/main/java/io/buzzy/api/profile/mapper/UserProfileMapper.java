package io.buzzy.api.profile.mapper;

import io.buzzy.api.profile.controller.model.SearchProfileDTO;
import io.buzzy.api.profile.controller.model.UserConnectionDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.common.messaging.model.NewConnectionDTO;
import io.buzzy.common.messaging.model.SuccessfulRegistrationDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "username", ignore = true)
    void update(@MappingTarget UserProfile userProfile, UserProfileDTO userProfileDTO);

    List<SearchProfileDTO> toSearchProfileDTOList(List<UserProfile> userProfile);

    default NewConnectionDTO toNewConnectionDTO(UUID requesterId, UUID connectionId) {
        NewConnectionDTO newConnectionDTO = new NewConnectionDTO();
        newConnectionDTO.setConnectionId(connectionId.toString());
        newConnectionDTO.setRequesterId(requesterId.toString());
        return newConnectionDTO;
    }

    default UUID mapToUUID(String id) {
        return UUID.fromString(id);
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
