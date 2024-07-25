package io.buzzy.websockets.server.messaging.model.web;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum UpdateEventType {
    online_offline_status, conversation_update, message_update
}
