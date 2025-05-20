package com.forumviajeros.backend.service.category;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.category.CategoryRequestDTO;
import com.forumviajeros.backend.dto.category.CategoryResponseDTO;

public interface CategoryService {
    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO findById(Long id);

    CategoryResponseDTO create(CategoryRequestDTO categoryDTO);

    CategoryResponseDTO update(Long id, CategoryRequestDTO categoryDTO);

    void delete(Long id);

    CategoryResponseDTO updateImage(Long id, MultipartFile file);
}
