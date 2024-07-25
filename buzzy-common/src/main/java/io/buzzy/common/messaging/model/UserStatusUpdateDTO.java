package io.buzzy.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserStatusUpdateDTO implements MessageDTO {
    private String username;
    private OffsetDateTime lastSeen;

    public UserStatusUpdateDTO() {
    }

    public UserStatusUpdateDTO(String username, OffsetDateTime lastSeen) {
        this.username = username;
        this.lastSeen = lastSeen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public OffsetDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(OffsetDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
}
