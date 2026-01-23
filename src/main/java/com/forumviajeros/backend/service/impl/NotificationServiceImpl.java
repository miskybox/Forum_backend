package com.forumviajeros.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forumviajeros.backend.model.Notification;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.NotificationRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public void createLikeNotification(User actor, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return;

        // No notificar si el usuario da like a su propio post
        if (post.getUser().getId().equals(actor.getId())) return;

        Notification notification = new Notification();
        notification.setUser(post.getUser());
        notification.setTipo("LIKE");
        notification.setReferenciaId(postId);
        notification.setLeido(false);
        notificationRepository.save(notification);
    }

    @Override
    public void createCommentNotification(User actor, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return;

        // No notificar si el usuario comenta en su propio post
        if (post.getUser().getId().equals(actor.getId())) return;

        Notification notification = new Notification();
        notification.setUser(post.getUser());
        notification.setTipo("COMMENT");
        notification.setReferenciaId(postId);
        notification.setLeido(false);
        notificationRepository.save(notification);
    }

    @Override
    public void createFollowNotification(User actor, User followed) {
        // No notificar si el usuario se sigue a sí mismo (edge case)
        if (actor.getId().equals(followed.getId())) return;

        Notification notification = new Notification();
        notification.setUser(followed);
        notification.setTipo("FOLLOW");
        notification.setReferenciaId(actor.getId());
        notification.setLeido(false);
        notificationRepository.save(notification);
    }

    @Override
    public void createShareNotification(User actor, Long postId, User recipient) {
        // No notificar si el usuario comparte consigo mismo
        if (actor.getId().equals(recipient.getId())) return;

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setTipo("SHARE");
        notification.setReferenciaId(postId);
        notification.setLeido(false);
        notificationRepository.save(notification);
    }
}
