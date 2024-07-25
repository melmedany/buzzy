package io.buzzy.websockets.server.messaging.service;

import io.buzzy.common.messaging.model.ConversationMessageUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConversationMessageUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationMessageUpdateService.class);

    private final ConversationMessageStateUpdateProducer conversationMessageStateUpdateProducer;

    public ConversationMessageUpdateService(ConversationMessageStateUpdateProducer conversationMessageStateUpdateProducer) {
        this.conversationMessageStateUpdateProducer = conversationMessageStateUpdateProducer;
    }

    public void setReadState(String conversationId, String messageId, String principalName) {
        ConversationMessageUpdateDTO conversationMessageUpdate = new ConversationMessageUpdateDTO();
        conversationMessageUpdate.setMessageId(messageId);
        conversationMessageUpdate.setConversationId(conversationId);
        conversationMessageUpdate.setUpdateInitiator(principalName);
        conversationMessageStateUpdateProducer.send(conversationMessageUpdate);
    }

    public void bulkSetReadState(String conversationId, String messageId, String principalName) {
        ConversationMessageUpdateDTO bulkUpdate = new ConversationMessageUpdateDTO();
        bulkUpdate.setLastReadMessageId(messageId);
        bulkUpdate.setConversationId(conversationId);
        bulkUpdate.setUpdateInitiator(principalName);
        conversationMessageStateUpdateProducer.send(bulkUpdate);
    }
}
