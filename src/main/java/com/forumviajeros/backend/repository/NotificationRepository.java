package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forumviajeros.backend.model.Notification;
import com.forumviajeros.backend.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserOrderByFechaDesc(User user);
    long countByUserAndLeidoFalse(User user);
    List<Notification> findByUserAndLeidoFalse(User user);
}
