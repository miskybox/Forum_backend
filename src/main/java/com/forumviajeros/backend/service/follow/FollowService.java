package com.forumviajeros.backend.service.follow;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.forumviajeros.backend.dto.follow.FollowResponseDTO;
import com.forumviajeros.backend.dto.follow.FollowStatsDTO;
import com.forumviajeros.backend.dto.follow.UserSummaryDTO;

public interface FollowService {

    /**
     * Seguir a un usuario
     */
    void followUser(Long userId, Authentication auth);

    /**
     * Dejar de seguir a un usuario
     */
    void unfollowUser(Long userId, Authentication auth);

    /**
     * Obtener lista de seguidores de un usuario
     */
    List<FollowResponseDTO> getFollowers(Long userId, Authentication auth);

    /**
     * Obtener lista de seguidores paginada
     */
    Page<FollowResponseDTO> getFollowersPaged(Long userId, Pageable pageable, Authentication auth);

    /**
     * Obtener lista de usuarios que sigue
     */
    List<FollowResponseDTO> getFollowing(Long userId, Authentication auth);

    /**
     * Obtener lista de usuarios que sigue paginada
     */
    Page<FollowResponseDTO> getFollowingPaged(Long userId, Pageable pageable, Authentication auth);

    /**
     * Obtener estadísticas de seguimiento de un usuario
     */
    FollowStatsDTO getFollowStats(Long userId, Authentication auth);

    /**
     * Verificar si el usuario actual sigue a otro usuario
     */
    boolean isFollowing(Long userId, Authentication auth);

    /**
     * Obtener sugerencias de usuarios para seguir
     */
    List<UserSummaryDTO> getSuggestedUsers(Authentication auth, Pageable pageable);

    /**
     * Obtener seguidores mutuos
     */
    List<UserSummaryDTO> getMutualFollows(Authentication auth);
}
