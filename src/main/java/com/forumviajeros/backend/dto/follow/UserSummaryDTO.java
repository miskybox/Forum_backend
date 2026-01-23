package com.forumviajeros.backend.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private String bio;
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowedByMe;
}
