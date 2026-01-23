package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.message.ConversationDTO;
import com.forumviajeros.backend.dto.message.MessageRequestDTO;
import com.forumviajeros.backend.dto.message.MessageResponseDTO;
import com.forumviajeros.backend.service.message.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class PrivateMessageController {

    private final MessageService messageService;

    /**
     * Enviar un mensaje privado
     */
    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @Valid @RequestBody MessageRequestDTO request,
            Authentication auth) {
        log.info("Usuario {} enviando mensaje a usuario {}", auth.getName(), request.getRecipientId());
        MessageResponseDTO response = messageService.sendMessage(request, auth);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener lista de conversaciones
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(Authentication auth) {
        List<ConversationDTO> conversations = messageService.getConversations(auth);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Obtener conversacion con un usuario
     */
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(
            @PathVariable Long userId,
            Authentication auth) {
        List<MessageResponseDTO> messages = messageService.getConversation(userId, auth);
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtener conversacion paginada
     */
    @GetMapping("/conversation/{userId}/paged")
    public ResponseEntity<Page<MessageResponseDTO>> getConversationPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponseDTO> messages = messageService.getConversationPaged(userId, pageable, auth);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marcar conversacion como leida
     */
    @PutMapping("/conversation/{userId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long userId,
            Authentication auth) {
        messageService.markConversationAsRead(userId, auth);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener cantidad de mensajes no leidos
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication auth) {
        Long count = messageService.getUnreadCount(auth);
        return ResponseEntity.ok(count);
    }

    /**
     * Eliminar un mensaje (solo el remitente puede)
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            Authentication auth) {
        messageService.deleteMessage(messageId, auth);
        return ResponseEntity.noContent().build();
    }
}
