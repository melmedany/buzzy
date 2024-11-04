package io.buzzy.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewConnectionDTO implements MessageDTO {
    private String requesterId;
    private String connectionId;

    public NewConnectionDTO() {
    }

    public NewConnectionDTO(String requesterId, String connectionId) {
        this.requesterId = requesterId;
        this.connectionId = connectionId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
