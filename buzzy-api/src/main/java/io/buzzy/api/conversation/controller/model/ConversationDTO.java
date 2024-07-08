package io.buzzy.api.conversation.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.buzzy.api.profile.controller.model.UserProfileDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationDTO {
    private String id;
    private String type;
    private String name;
    private String avatar;
    private List<String> admins;
    private List<ConversationMessageDTO> messages;
    @JsonProperty("contacts")
    private List<UserProfileDTO> participants;
    private ConversationConfigurationDTO configuration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public List<ConversationMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ConversationMessageDTO> messages) {
        this.messages = messages;
    }

    public List<UserProfileDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserProfileDTO> participants) {
        this.participants = participants;
    }

    public ConversationConfigurationDTO getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConversationConfigurationDTO configuration) {
        this.configuration = configuration;
    }
}
