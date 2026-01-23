package com.forumviajeros.backend.service;

import com.forumviajeros.backend.model.User;

public interface NotificationService {
    void createLikeNotification(User actor, Long postId);
    void createCommentNotification(User actor, Long postId);
    void createFollowNotification(User actor, User followed);
    void createShareNotification(User actor, Long postId, User recipient);
}
