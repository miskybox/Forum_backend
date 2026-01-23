package com.forumviajeros.backend.service;

import com.forumviajeros.backend.dto.PostLikeDTO;

public interface PostLikeService {

    PostLikeDTO toggleLike(Long postId, Long userId);

    boolean hasUserLiked(Long postId, Long userId);

    long getLikeCount(Long postId);

    PostLikeDTO getLikeStatus(Long postId, Long userId);
}
