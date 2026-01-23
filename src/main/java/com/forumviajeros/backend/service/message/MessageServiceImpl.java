package com.forumviajeros.backend.service.message;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.message.ConversationDTO;
import com.forumviajeros.backend.dto.message.MessageRequestDTO;
import com.forumviajeros.backend.dto.message.MessageResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.PrivateMessage;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.PrivateMessageRepository;
import com.forumviajeros.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {

    private final PrivateMessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponseDTO sendMessage(MessageRequestDTO request, Authentication auth) {
        User sender = getCurrentUser(auth);
        User recipient = userRepository.findById(request.getRecipientId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRecipientId()));

        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("No puedes enviarte mensajes a ti mismo");
        }

        PrivateMessage message = PrivateMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .build();

        message = messageRepository.save(message);
        log.info("Mensaje enviado de {} a {}", sender.getUsername(), recipient.getUsername());

        return mapToDTO(message, sender);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversations(Authentication auth) {
        User currentUser = getCurrentUser(auth);
        List<PrivateMessage> latestMessages = messageRepository.findLatestConversations(currentUser.getId());

        return latestMessages.stream()
                .map(msg -> {
                    User participant = msg.getSender().getId().equals(currentUser.getId())
                            ? msg.getRecipient()
                            : msg.getSender();

                    Long unreadCount = messageRepository.countByRecipientAndSenderAndIsReadFalse(
                            currentUser, participant);

                    return ConversationDTO.builder()
                            .participantId(participant.getId())
                            .participantUsername(participant.getUsername())
                            .participantAvatarUrl(participant.getAvatarUrl())
                            .lastMessage(truncateMessage(msg.getContent()))
                            .lastMessageAt(msg.getSentAt())
                            .isLastMessageMine(msg.getSender().getId().equals(currentUser.getId()))
                            .unreadCount(unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversation(Long userId, Authentication auth) {
        User currentUser = getCurrentUser(auth);
        User otherUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<PrivateMessage> messages = messageRepository.findConversation(currentUser, otherUser);

        return messages.stream()
                .map(msg -> mapToDTO(msg, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseDTO> getConversationPaged(Long userId, Pageable pageable, Authentication auth) {
        User currentUser = getCurrentUser(auth);
        User otherUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return messageRepository.findConversationPaged(currentUser, otherUser, pageable)
                .map(msg -> mapToDTO(msg, currentUser));
    }

    @Override
    public void markConversationAsRead(Long userId, Authentication auth) {
        User currentUser = getCurrentUser(auth);
        User sender = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        int updated = messageRepository.markConversationAsRead(currentUser, sender);
        log.info("Marcados {} mensajes como leidos de {} para {}", updated, sender.getUsername(), currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Authentication auth) {
        User currentUser = getCurrentUser(auth);
        return messageRepository.countByRecipientAndIsReadFalse(currentUser);
    }

    @Override
    public void deleteMessage(Long messageId, Authentication auth) {
        User currentUser = getCurrentUser(auth);
        PrivateMessage message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("PrivateMessage", "id", messageId));

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("No puedes eliminar mensajes de otros usuarios");
        }

        messageRepository.delete(message);
        log.info("Mensaje {} eliminado por {}", messageId, currentUser.getUsername());
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
    }

    private MessageResponseDTO mapToDTO(PrivateMessage message, User currentUser) {
        return MessageResponseDTO.builder()
            .id(message.getId())
            .senderId(message.getSender().getId())
            .senderUsername(message.getSender().getUsername())
            .senderAvatarUrl(message.getSender().getAvatarUrl())
            .recipientId(message.getRecipient().getId())
            .recipientUsername(message.getRecipient().getUsername())
            .recipientAvatarUrl(message.getRecipient().getAvatarUrl())
            .content(message.getContent())
            .sentAt(message.getSentAt())
            .readAt(message.getReadAt())
            .isRead(message.getIsRead())
            .isMine(message.getSender().getId().equals(currentUser.getId()))
            .build();
    }

    private String truncateMessage(String content) {
        if (content == null) return "";
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}
