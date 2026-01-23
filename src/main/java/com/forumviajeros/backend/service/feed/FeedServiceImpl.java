package com.forumviajeros.backend.service.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.feed.FeedItemDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.FollowRepository;
import com.forumviajeros.backend.repository.PostLikeRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public Page<FeedItemDTO> getFollowingFeed(Pageable pageable, Authentication auth) {
        User currentUser = getCurrentUser(auth);

        List<Long> followingIds = followRepository.findFollowingUserIds(currentUser);

        if (followingIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Post> posts = postRepository.findByUserIdInAndStatus(
                followingIds,
                Post.PostStatus.ACTIVE,
                pageable
        );

        return posts.map(this::mapToFeedItem);
    }

    @Override
    public Page<FeedItemDTO> getExploreFeed(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Post> posts = postRepository.findAll(sortedPageable);

        return posts.map(this::mapToFeedItem);
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
    }

    private FeedItemDTO mapToFeedItem(Post post) {
        long likeCount = postLikeRepository.countByPostId(post.getId());
        return FeedItemDTO.builder()
                .id(post.getId())
                .type("POST")
                .title(post.getTitle())
                .content(truncateContent(post.getContent()))
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .authorId(post.getUser().getId())
                .authorUsername(post.getUser().getUsername())
                .authorAvatarUrl(post.getUser().getAvatarUrl())
                .forumId(post.getForum().getId())
                .forumName(post.getForum().getTitle())
                .postId(post.getId())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .likeCount((int) likeCount)
                .build();
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.length() > 200 ? content.substring(0, 200) + "..." : content;
    }
}
