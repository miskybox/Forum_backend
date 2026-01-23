package com.forumviajeros.backend.dto.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    private Long id;
    private Long senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private Long recipientId;
    private String recipientUsername;
    private String recipientAvatarUrl;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Boolean isRead;
    private Boolean isMine;
}
