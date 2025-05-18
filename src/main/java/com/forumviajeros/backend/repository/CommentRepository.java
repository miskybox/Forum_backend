package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Comment;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);

    List<Comment> findByPost(Post post);

    Page<Comment> findByPost(Post post, Pageable pageable);

    Page<Comment> findByUser(User user, Pageable pageable);

    List<Comment> findByPostId(Long postId);
}