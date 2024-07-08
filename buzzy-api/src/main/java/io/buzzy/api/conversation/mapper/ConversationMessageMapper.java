package io.buzzy.api.conversation.mapper;

import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.common.messaging.model.PostMessageUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConversationMessageMapper {
    @Mapping(target = "date", source = "created")
    ConversationMessageDTO toDTO(ConversationMessage conversationMessage);

    ConversationMessage toEntity(ConversationMessageDTO conversationMessage);

    default PostMessageUpdateDTO toPostMessageUpdateDTO(String senderUsername, Conversation conversation, ConversationMessage conversationMessage) {
        PostMessageUpdateDTO postMessageUpdateDTO = new PostMessageUpdateDTO();
        postMessageUpdateDTO.setConversationId(conversation.getId().toString());

        ConversationMessageDTO conversationMessageDTO = toDTO(conversationMessage);
        postMessageUpdateDTO.setId(conversationMessageDTO.getId());
        postMessageUpdateDTO.setSenderUsername(senderUsername);
        postMessageUpdateDTO.setText(conversationMessageDTO.getText());
        postMessageUpdateDTO.setState(conversationMessageDTO.getState().toString());
        postMessageUpdateDTO.setType(conversationMessageDTO.getType());
        postMessageUpdateDTO.setDate(conversationMessageDTO.getDate());


        List<String> receivers = conversation.getParticipants().stream()
                .filter(profile -> !conversationMessage.getSender().equals(profile.getId()))
                .map(UserProfile::getUsername)
                .toList();

        postMessageUpdateDTO.setReceiversUsernames(receivers);

        return postMessageUpdateDTO;
    }
}
