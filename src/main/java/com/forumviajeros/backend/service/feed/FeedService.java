package com.forumviajeros.backend.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.forumviajeros.backend.dto.feed.FeedItemDTO;

public interface FeedService {

    /**
     * Get feed from users that the current user follows
     */
    Page<FeedItemDTO> getFollowingFeed(Pageable pageable, Authentication auth);

    /**
     * Get global/explore feed with recent posts
     */
    Page<FeedItemDTO> getExploreFeed(Pageable pageable);
}
