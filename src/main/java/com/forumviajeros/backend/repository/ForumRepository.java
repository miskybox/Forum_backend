package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.User;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findByUser(User user);

    List<Forum> findByCategory(Category category);

    Page<Forum> findByCategory(Category category, Pageable pageable);

    Page<Forum> findByCategoryAndStatus(Category category, Forum.ForumStatus status, Pageable pageable);

    Page<Forum> findByUser(User user, Pageable pageable);

    @Query("SELECT f FROM Forum f WHERE f.title LIKE %:keyword% OR f.description LIKE %:keyword%")
    Page<Forum> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT f FROM Forum f JOIN f.tags t WHERE t.name = :tagName")
    Page<Forum> findByTagName(@Param("tagName") String tagName, Pageable pageable);
}