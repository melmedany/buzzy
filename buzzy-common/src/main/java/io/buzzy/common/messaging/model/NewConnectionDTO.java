package io.buzzy.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewConnectionDTO implements MessageDTO {
    private String userId;
    private String connectionId;

    public NewConnectionDTO() {
    }

    public NewConnectionDTO(String userId, String connectionId) {
        this.userId = userId;
        this.connectionId = connectionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
