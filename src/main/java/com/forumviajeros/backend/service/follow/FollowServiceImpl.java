package com.forumviajeros.backend.service.follow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.follow.FollowResponseDTO;
import com.forumviajeros.backend.dto.follow.FollowStatsDTO;
import com.forumviajeros.backend.dto.follow.UserSummaryDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Follow;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.FollowRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public void followUser(Long userId, Authentication auth) {
        User follower = getCurrentUser(auth);
        User followed = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (follower.getId().equals(followed.getId())) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo");
        }

        if (followRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new IllegalArgumentException("Ya sigues a este usuario");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followed(followed)
                .build();

        followRepository.save(follow);
        // Generar notificación de follow
        notificationService.createFollowNotification(follower, followed);
        log.info("Usuario {} ahora sigue a {}", follower.getUsername(), followed.getUsername());
    }

    @Override
    public void unfollowUser(Long userId, Authentication auth) {
        User follower = getCurrentUser(auth);
        User followed = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new IllegalArgumentException("No sigues a este usuario"));

        followRepository.delete(follow);
        log.info("Usuario {} dejo de seguir a {}", follower.getUsername(), followed.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowResponseDTO> getFollowers(Long userId, Authentication auth) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User currentUser = auth != null ? getCurrentUser(auth) : null;

        List<User> followers = followRepository.findFollowerUsers(user);

        return followers.stream()
                .map(follower -> mapToFollowResponseDTO(follower, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowersPaged(Long userId, Pageable pageable, Authentication auth) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User currentUser = auth != null ? getCurrentUser(auth) : null;

        return followRepository.findByFollowedOrderByCreatedAtDesc(user, pageable)
                .map(follow -> mapToFollowResponseDTO(follow.getFollower(), currentUser, follow.getCreatedAt()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowResponseDTO> getFollowing(Long userId, Authentication auth) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User currentUser = auth != null ? getCurrentUser(auth) : null;

        List<User> following = followRepository.findFollowingUsers(user);

        return following.stream()
                .map(followed -> mapToFollowResponseDTO(followed, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowingPaged(Long userId, Pageable pageable, Authentication auth) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        User currentUser = auth != null ? getCurrentUser(auth) : null;

        return followRepository.findByFollowerOrderByCreatedAtDesc(user, pageable)
                .map(follow -> mapToFollowResponseDTO(follow.getFollowed(), currentUser, follow.getCreatedAt()));
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatsDTO getFollowStats(Long userId, Authentication auth) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Long followersCount = followRepository.countByFollowed(user);
        Long followingCount = followRepository.countByFollower(user);

        Boolean isFollowedByMe = false;
        Boolean isFollowingMe = false;

        if (auth != null) {
            User currentUser = getCurrentUser(auth);
            if (!currentUser.getId().equals(userId)) {
                isFollowedByMe = followRepository.isFollowing(currentUser, user);
                isFollowingMe = followRepository.isFollowing(user, currentUser);
            }
        }

        return FollowStatsDTO.builder()
                .userId(userId)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowedByMe(isFollowedByMe)
                .isFollowingMe(isFollowingMe)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long userId, Authentication auth) {
        User currentUser = getCurrentUser(auth);
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return followRepository.isFollowing(currentUser, targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getSuggestedUsers(Authentication auth, Pageable pageable) {
        User currentUser = getCurrentUser(auth);
        List<User> suggestions = followRepository.findSuggestedUsers(currentUser, pageable);

        return suggestions.stream()
                .map(user -> mapToUserSummaryDTO(user, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getMutualFollows(Authentication auth) {
        User currentUser = getCurrentUser(auth);
        List<User> mutuals = followRepository.findMutualFollows(currentUser);

        return mutuals.stream()
                .map(user -> mapToUserSummaryDTO(user, currentUser))
                .collect(Collectors.toList());
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
    }

    private FollowResponseDTO mapToFollowResponseDTO(User user, User currentUser) {
        return mapToFollowResponseDTO(user, currentUser, null);
    }

    private FollowResponseDTO mapToFollowResponseDTO(User user, User currentUser, java.time.LocalDateTime followedAt) {
        Boolean isFollowingBack = false;
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            isFollowingBack = followRepository.isFollowing(currentUser, user);
        }

        return FollowResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .followedAt(followedAt)
                .isFollowingBack(isFollowingBack)
                .build();
    }

    private UserSummaryDTO mapToUserSummaryDTO(User user, User currentUser) {
        Long followersCount = followRepository.countByFollowed(user);
        Long followingCount = followRepository.countByFollower(user);
        Boolean isFollowedByMe = false;

        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            isFollowedByMe = followRepository.isFollowing(currentUser, user);
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowedByMe(isFollowedByMe)
                .build();
    }
}
