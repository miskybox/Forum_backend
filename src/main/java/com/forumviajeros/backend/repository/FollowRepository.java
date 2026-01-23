package com.forumviajeros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.forumviajeros.backend.model.Follow;
import com.forumviajeros.backend.model.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFollowedId(Long followedId);

    // Paginación
    Page<Follow> findByFollowerOrderByCreatedAtDesc(User follower, Pageable pageable);
    Page<Follow> findByFollowedOrderByCreatedAtDesc(User followed, Pageable pageable);

    // Conteos
    Long countByFollower(User follower);
    Long countByFollowed(User followed);

    // Obtener usuarios que sigo
    @Query("SELECT f.followed FROM Follow f WHERE f.follower = :user ORDER BY f.createdAt DESC")
    List<User> findFollowingUsers(@Param("user") User user);

    // Obtener mis seguidores
    @Query("SELECT f.follower FROM Follow f WHERE f.followed = :user ORDER BY f.createdAt DESC")
    List<User> findFollowerUsers(@Param("user") User user);

    // Obtener IDs de usuarios que sigo (útil para feeds)
    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower = :user")
    List<Long> findFollowingUserIds(@Param("user") User user);

    // Verificar seguimiento mutuo
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f " +
           "WHERE f.follower = :user1 AND f.followed = :user2")
    boolean isFollowing(@Param("user1") User user1, @Param("user2") User user2);

    // Seguidores mutuos (se siguen entre sí)
    @Query("SELECT f1.followed FROM Follow f1 " +
           "WHERE f1.follower = :user " +
           "AND EXISTS (SELECT f2 FROM Follow f2 WHERE f2.follower = f1.followed AND f2.followed = :user)")
    List<User> findMutualFollows(@Param("user") User user);

    // Sugerencias de usuarios (seguidores de mis seguidores que no sigo)
    @Query("SELECT DISTINCT f2.followed FROM Follow f1 " +
           "JOIN Follow f2 ON f2.follower = f1.followed " +
           "WHERE f1.follower = :user " +
           "AND f2.followed <> :user " +
           "AND NOT EXISTS (SELECT f3 FROM Follow f3 WHERE f3.follower = :user AND f3.followed = f2.followed)")
    List<User> findSuggestedUsers(@Param("user") User user, Pageable pageable);
}
