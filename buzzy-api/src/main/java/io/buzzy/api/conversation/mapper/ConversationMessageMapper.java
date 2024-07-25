package io.buzzy.api.conversation.mapper;

import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConversationMessageMapper {
    @Mapping(target = "date", source = "created")
    ConversationMessageDTO toDTO(ConversationMessage conversationMessage);

    default ConversationMessageUpdateDTO toConversationMessageUpdateDTO(Conversation conversation,
                                                                        ConversationMessage conversationMessage,
                                                                        boolean selfUpdate) {
        ConversationMessageUpdateDTO conversationMessageUpdateDTO = new ConversationMessageUpdateDTO();
        conversationMessageUpdateDTO.setConversationId(conversation.getId().toString());

        ConversationMessageDTO conversationMessageDTO = toDTO(conversationMessage);
        conversationMessageUpdateDTO.setMessageId(conversationMessageDTO.getId());
        conversationMessageUpdateDTO.setState(conversationMessageDTO.getState().toString());

        List<String> receivers;

        if (selfUpdate) {
            receivers = conversation.getParticipants().stream()
                    .map(UserProfile::getUsername)
                    .toList();
        } else {
            receivers = conversation.getParticipants().stream()
                    .filter(profile -> !conversationMessage.getSender().getId().equals(profile.getId()))
                    .map(UserProfile::getUsername)
                    .toList();
        }

        conversationMessageUpdateDTO.setReceiversUsernames(receivers);

        return conversationMessageUpdateDTO;
    }
}
