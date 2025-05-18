package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPostId(Long postId);

}