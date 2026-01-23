package com.forumviajeros.backend.service.message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.forumviajeros.backend.dto.message.ConversationDTO;
import com.forumviajeros.backend.dto.message.MessageRequestDTO;
import com.forumviajeros.backend.dto.message.MessageResponseDTO;

public interface MessageService {

    MessageResponseDTO sendMessage(MessageRequestDTO request, Authentication auth);

    List<ConversationDTO> getConversations(Authentication auth);

    List<MessageResponseDTO> getConversation(Long userId, Authentication auth);

    Page<MessageResponseDTO> getConversationPaged(Long userId, Pageable pageable, Authentication auth);

    void markConversationAsRead(Long userId, Authentication auth);

    Long getUnreadCount(Authentication auth);

    void deleteMessage(Long messageId, Authentication auth);
}
