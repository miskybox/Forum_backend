package com.forumviajeros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    List<Tag> findByNameIn(List<String> names);

    List<Tag> findByNameStartingWithIgnoreCase(String name);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT t FROM Tag t LEFT JOIN t.posts p GROUP BY t.id ORDER BY COUNT(p) DESC")
    Page<Tag> findMostPopularTags(Pageable pageable);
}