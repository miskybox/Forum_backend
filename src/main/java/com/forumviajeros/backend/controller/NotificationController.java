package com.forumviajeros.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.model.Notification;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.NotificationRepository;
import com.forumviajeros.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    // Listar notificaciones del usuario autenticado
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        List<Notification> notifications = notificationRepository.findByUserOrderByFechaDesc(user);
        return ResponseEntity.ok(notifications);
    }

    // Obtener conteo de notificaciones no leídas
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        long count = notificationRepository.countByUserAndLeidoFalse(user);
        return ResponseEntity.ok(count);
    }

    // Marcar notificación como leída
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (user == null || notification == null || !notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body("Operación no válida");
        }
        notification.setLeido(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok("Notificación marcada como leída");
    }

    // Marcar todas las notificaciones como leídas
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        List<Notification> unread = notificationRepository.findByUserAndLeidoFalse(user);
        for (Notification n : unread) {
            n.setLeido(true);
        }
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok("Todas las notificaciones marcadas como leídas");
    }
}
