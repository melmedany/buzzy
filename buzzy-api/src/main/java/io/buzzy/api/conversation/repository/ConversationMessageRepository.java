package io.buzzy.api.conversation.repository;

import io.buzzy.api.conversation.model.ConversationMessageState;
import io.buzzy.api.conversation.repository.entity.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, UUID> {

    Optional<ConversationMessage> findByIdAndConversationId(UUID id, UUID conversationId);

    @Modifying
    @Query("UPDATE ConversationMessage cm SET cm.state = :state WHERE cm.conversation.id = :conversationId and cm.created >= (SELECT cm2.created FROM ConversationMessage cm2 WHERE cm2.id = :id and cm2.conversation.id = :conversationId)")
    int bulkUpdateState(UUID id, UUID conversationId, ConversationMessageState state);

    @Query("SELECT cm FROM ConversationMessage cm WHERE cm.state <> :state AND cm.conversation.id = :conversationId and cm.created >= (SELECT cm2.created FROM ConversationMessage cm2 WHERE cm2.id = :id and cm2.conversation.id = :conversationId)")
    List<ConversationMessage> findForBulkUpdateState(UUID id, UUID conversationId, ConversationMessageState state);

}
