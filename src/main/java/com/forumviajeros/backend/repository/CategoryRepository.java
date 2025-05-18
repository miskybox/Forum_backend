package com.forumviajeros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(String type);

    Optional<Category> findByName(String name);

    Boolean existsByName(String name);
}
