package io.buzzy.api.conversation.mapper;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConversationMapper {

    ConversationDTO toDTO(Conversation conversation);

    @Mapping(source = "created", target = "date")
    ConversationMessageDTO toConversationMessageDTO(ConversationMessage message);

    List<ConversationDTO> toDTOList(List<Conversation> conversations);
    Conversation toEntity(ConversationDTO conversation);
}
