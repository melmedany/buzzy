package io.buzzy.api.conversation.service;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.mapper.ConversationMapper;
import io.buzzy.api.conversation.model.ConversationType;
import io.buzzy.api.conversation.repository.ConversationRepository;
import io.buzzy.api.conversation.repository.entity.Conversation;
import io.buzzy.api.conversation.repository.entity.ConversationConfiguration;
import io.buzzy.api.conversation.service.exception.ConversationNotFoundException;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.repository.entity.UserProfile;
import io.buzzy.api.profile.service.UserProfileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final UserProfileService userProfileService;
    private final ConversationMapper conversationMapper;

    public ConversationService(ConversationRepository conversationRepository,
                               UserProfileService userProfileService,
                               ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.userProfileService = userProfileService;
        this.conversationMapper = conversationMapper;
    }

    public List<ConversationDTO> getConversationsSummary(String username) {
        List<Conversation> conversations = conversationRepository.findConversationSummaryForUser(getUserProfileId(username));
        List<ConversationDTO> summary = conversationMapper.toDTOList(conversations);
        summary.forEach(conversation -> setName(conversation, username));
        return summary;
    }

    public ConversationDTO getConversation(String id, String username) {
        Conversation conversation = conversationRepository.findById(UUID.fromString(id)).orElse(null);

        if (conversation == null) {
            throw new ConversationNotFoundException(id, "Conversation not found!");
        }

        boolean userInConversationParticipants = conversation.getParticipants().stream()
                .anyMatch(participant -> username.equalsIgnoreCase(participant.getUsername()));

        if (!userInConversationParticipants) {
            throw new ConversationNotFoundException(id, "Conversation not found!");
        }

        return conversationMapper.toDTO(conversation);
    }

    public void createConversation(UserProfile newProfile) {
        ConversationConfiguration configuration = new ConversationConfiguration();

        Conversation firstConversation = new Conversation();
        firstConversation.setType(ConversationType.direct_message);
        firstConversation.setConfiguration(configuration);

        firstConversation.setParticipants(List.of(newProfile));

        configuration.setConversation(firstConversation);

        conversationRepository.save(firstConversation);
    }

    public void createConversation(UserProfile user, UserProfile connection) {
        ConversationConfiguration configuration = new ConversationConfiguration();

        Conversation newConversation = new Conversation();
        newConversation.setType(ConversationType.direct_message);
        newConversation.setConfiguration(configuration);

        newConversation.setParticipants(List.of(user, connection));

        configuration.setConversation(newConversation);

        conversationRepository.save(newConversation);
    }

    private void setName(ConversationDTO conversationDTO, String currentUsername) {
        if (conversationDTO.getConfiguration() != null && StringUtils.isNotBlank(conversationDTO.getConfiguration().getName())) {
            conversationDTO.setName(conversationDTO.getConfiguration().getName());
            return;
        }

        if (conversationDTO.getParticipants().size() == 1) {
            conversationDTO.setName(conversationDTO.getParticipants().getFirst().getFirstname());
            return;
        }

        String name = conversationDTO.getParticipants().stream()
                .filter(p -> !p.getUsername().equals(currentUsername))
                .findFirst()
                .map(UserProfileDTO::getFirstname)
                .orElse("Generic Member");

        conversationDTO.setName(name);
    }

    private UUID getUserProfileId(String username) {
        return userProfileService.findByUsername(username).getId();
    }
}
