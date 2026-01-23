package com.forumviajeros.backend.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsDTO {
    private Long userId;
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowedByMe;
    private Boolean isFollowingMe;
}
