package io.buzzy.websockets.server.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum UpdateEventType {
    online_offline_status, post_message, conversation_update
}
