package io.buzzy.api.conversation.repository;

import io.buzzy.api.conversation.repository.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c JOIN c.participants p LEFT JOIN FETCH c.messages cm WHERE p.id = :userId AND (c.messages IS EMPTY OR cm.id = (SELECT cm2.id FROM Conversation c2 JOIN c2.messages cm2 WHERE c2.id = c.id ORDER BY cm2.created DESC LIMIT 1)) ORDER BY cm.created")
    List<Conversation> findConversationSummaryForUser(@Param("userId") UUID userId);

}