package com.forumviajeros.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeDTO {
    private Long postId;
    private boolean liked;
    private long likeCount;
}
