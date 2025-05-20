package com.forumviajeros.backend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private String userAvatar;
    private String status;
    private String createdAt;
    private String updatedAt;
}