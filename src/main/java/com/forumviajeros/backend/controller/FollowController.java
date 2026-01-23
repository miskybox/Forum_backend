package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.follow.FollowResponseDTO;
import com.forumviajeros.backend.dto.follow.FollowStatsDTO;
import com.forumviajeros.backend.dto.follow.UserSummaryDTO;
import com.forumviajeros.backend.service.follow.FollowService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final FollowService followService;

    /**
     * Seguir a un usuario
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long id, Authentication auth) {
        log.info("Usuario {} siguiendo a usuario {}", auth.getName(), id);
        followService.followUser(id, auth);
        return ResponseEntity.ok().build();
    }

    /**
     * Dejar de seguir a un usuario
     */
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long id, Authentication auth) {
        log.info("Usuario {} dejando de seguir a usuario {}", auth.getName(), id);
        followService.unfollowUser(id, auth);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener seguidores de un usuario
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<List<FollowResponseDTO>> getFollowers(
            @PathVariable Long id,
            Authentication auth) {
        List<FollowResponseDTO> followers = followService.getFollowers(id, auth);
        return ResponseEntity.ok(followers);
    }

    /**
     * Obtener seguidores paginados
     */
    @GetMapping("/{id}/followers/paged")
    public ResponseEntity<Page<FollowResponseDTO>> getFollowersPaged(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FollowResponseDTO> followers = followService.getFollowersPaged(id, pageable, auth);
        return ResponseEntity.ok(followers);
    }

    /**
     * Obtener usuarios que sigue
     */
    @GetMapping("/{id}/following")
    public ResponseEntity<List<FollowResponseDTO>> getFollowing(
            @PathVariable Long id,
            Authentication auth) {
        List<FollowResponseDTO> following = followService.getFollowing(id, auth);
        return ResponseEntity.ok(following);
    }

    /**
     * Obtener usuarios que sigue paginados
     */
    @GetMapping("/{id}/following/paged")
    public ResponseEntity<Page<FollowResponseDTO>> getFollowingPaged(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FollowResponseDTO> following = followService.getFollowingPaged(id, pageable, auth);
        return ResponseEntity.ok(following);
    }

    /**
     * Obtener estadísticas de seguimiento
     */
    @GetMapping("/{id}/follow-stats")
    public ResponseEntity<FollowStatsDTO> getFollowStats(
            @PathVariable Long id,
            Authentication auth) {
        FollowStatsDTO stats = followService.getFollowStats(id, auth);
        return ResponseEntity.ok(stats);
    }

    /**
     * Verificar si el usuario actual sigue a otro usuario
     */
    @GetMapping("/{id}/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long id,
            Authentication auth) {
        boolean following = followService.isFollowing(id, auth);
        return ResponseEntity.ok(following);
    }

    /**
     * Obtener sugerencias de usuarios para seguir
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<UserSummaryDTO>> getSuggestedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserSummaryDTO> suggestions = followService.getSuggestedUsers(auth, pageable);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Obtener seguidores mutuos
     */
    @GetMapping("/mutuals")
    public ResponseEntity<List<UserSummaryDTO>> getMutualFollows(Authentication auth) {
        List<UserSummaryDTO> mutuals = followService.getMutualFollows(auth);
        return ResponseEntity.ok(mutuals);
    }
}
