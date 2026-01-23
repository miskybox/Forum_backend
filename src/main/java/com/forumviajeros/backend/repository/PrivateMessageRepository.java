package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.forumviajeros.backend.model.PrivateMessage;
import com.forumviajeros.backend.model.User;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    List<PrivateMessage> findByRecipientOrderBySentAtDesc(User recipient);

    List<PrivateMessage> findBySenderOrderBySentAtDesc(User sender);

    Page<PrivateMessage> findByRecipientOrderBySentAtDesc(User recipient, Pageable pageable);

    Page<PrivateMessage> findBySenderOrderBySentAtDesc(User sender, Pageable pageable);

    // Conversacion entre dos usuarios
    @Query("SELECT m FROM PrivateMessage m WHERE " +
           "(m.sender = :user1 AND m.recipient = :user2) OR " +
           "(m.sender = :user2 AND m.recipient = :user1) " +
           "ORDER BY m.sentAt ASC")
    List<PrivateMessage> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT m FROM PrivateMessage m WHERE " +
           "(m.sender = :user1 AND m.recipient = :user2) OR " +
           "(m.sender = :user2 AND m.recipient = :user1) " +
           "ORDER BY m.sentAt DESC")
    Page<PrivateMessage> findConversationPaged(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    // Contar mensajes no leidos
    Long countByRecipientAndIsReadFalse(User recipient);

    // Contar no leidos de un remitente especifico
    Long countByRecipientAndSenderAndIsReadFalse(User recipient, User sender);

    // Obtener lista de conversaciones (ultimos mensajes con cada usuario)
    // Query nativo compatible con H2 y PostgreSQL
    @Query(value = "SELECT pm.* FROM private_messages pm " +
           "INNER JOIN (" +
           "  SELECT MAX(id) as max_id FROM private_messages " +
           "  WHERE sender_id = :userId OR recipient_id = :userId " +
           "  GROUP BY LEAST(sender_id, recipient_id), GREATEST(sender_id, recipient_id)" +
           ") latest ON pm.id = latest.max_id " +
           "ORDER BY pm.sent_at DESC", nativeQuery = true)
    List<PrivateMessage> findLatestConversations(@Param("userId") Long userId);

    // Marcar mensajes como leidos
    @Modifying
    @Query("UPDATE PrivateMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP " +
           "WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false")
    int markConversationAsRead(@Param("recipient") User recipient, @Param("sender") User sender);
}
