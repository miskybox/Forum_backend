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
public class ConversationDTO {

    private Long participantId;
    private String participantUsername;
    private String participantAvatarUrl;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Boolean isLastMessageMine;
    private Long unreadCount;
}
