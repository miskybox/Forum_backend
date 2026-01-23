package com.forumviajeros.backend.dto.follow;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String bio;
    private LocalDateTime followedAt;
    private Boolean isFollowingBack;
}
