package com.forumviajeros.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.feed.FeedItemDTO;
import com.forumviajeros.backend.service.feed.FeedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;

    /**
     * Get feed from users that the current user follows
     */
    @GetMapping("/following")
    public ResponseEntity<Page<FeedItemDTO>> getFollowingFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        log.info("Usuario {} solicitando feed de seguidos", auth.getName());
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemDTO> feed = feedService.getFollowingFeed(pageable, auth);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get global/explore feed with recent posts
     */
    @GetMapping("/explore")
    public ResponseEntity<Page<FeedItemDTO>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Solicitando feed de explorar");
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemDTO> feed = feedService.getExploreFeed(pageable);
        return ResponseEntity.ok(feed);
    }
}
