package com.forumviajeros.backend.dto.feed;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedItemDTO {
    private Long id;
    private String type; // POST, COMMENT, etc.
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    // Author info
    private Long authorId;
    private String authorUsername;
    private String authorAvatarUrl;

    // Related entity info
    private Long forumId;
    private String forumName;
    private Long postId;

    // Engagement
    private Integer commentCount;
    private Integer likeCount;
}
