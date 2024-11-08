package io.buzzy.api.conversation.mapper;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConversationMapper {

    @Mapping(target = "admins", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "name", ignore = true)
    ConversationDTO toDTO(Conversation conversation);

    @Mapping(source = "created", target = "date")
    ConversationMessageDTO toConversationMessageDTO(ConversationMessage message);

    List<ConversationDTO> toDTOList(List<Conversation> conversations);

    default UserProfileDTO userProfileToUserProfileDTO(UserProfile userProfile) {
        if ( userProfile == null ) {
            return null;
        }

        UserProfileDTO userProfileDTO = new UserProfileDTO();

        userProfileDTO.setId( userProfile.getId() );
        userProfileDTO.setUsername( userProfile.getUsername() );
        userProfileDTO.setFirstname( userProfile.getFirstname() );
        userProfileDTO.setLastname( userProfile.getLastname() );
        userProfileDTO.setLastSeen( userProfile.getLastSeen() );
        return userProfileDTO;
    }
}
