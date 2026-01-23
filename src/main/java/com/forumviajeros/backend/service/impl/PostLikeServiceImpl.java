package com.forumviajeros.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.PostLikeDTO;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.PostLike;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.PostLikeRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.NotificationService;
import com.forumviajeros.backend.service.PostLikeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostLikeDTO toggleLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        boolean liked;
        var existingLike = postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            PostLike newLike = new PostLike();
            newLike.setUser(user);
            newLike.setPost(post);
            postLikeRepository.save(newLike);
            liked = true;
            // Generar notificación de like
            notificationService.createLikeNotification(user, postId);
        }

        long likeCount = postLikeRepository.countByPostId(postId);
        return new PostLikeDTO(postId, liked, likeCount);
    }

    @Override
    public boolean hasUserLiked(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public long getLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    public PostLikeDTO getLikeStatus(Long postId, Long userId) {
        boolean liked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, postId);
        long likeCount = postLikeRepository.countByPostId(postId);
        return new PostLikeDTO(postId, liked, likeCount);
    }
}
