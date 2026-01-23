package com.forumviajeros.backend.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.PostLikeDTO;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.PostLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final UserRepository userRepository;

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostLikeDTO> toggleLike(@PathVariable Long postId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PostLikeDTO result = postLikeService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{postId}/like")
    public ResponseEntity<PostLikeDTO> getLikeStatus(@PathVariable Long postId, Principal principal) {
        Long userId = null;
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }

        PostLikeDTO result = postLikeService.getLikeStatus(postId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{postId}/like/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long count = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }
}
