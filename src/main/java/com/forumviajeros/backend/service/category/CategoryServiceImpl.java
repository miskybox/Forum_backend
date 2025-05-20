package com.forumviajeros.backend.service.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.category.CategoryRequestDTO;
import com.forumviajeros.backend.dto.category.CategoryResponseDTO;
import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.repository.CategoryRepository;
import com.forumviajeros.backend.service.storage.LocalStorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final LocalStorageService localStorageService;

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));
        return toResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setType(categoryDTO.getType());
        Category saved = categoryRepository.save(category);
        return toResponseDTO(saved);
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));

        if (!category.getName().equals(categoryDTO.getName())
                && categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setType(categoryDTO.getType());
        Category updated = categoryRepository.save(category);
        return toResponseDTO(updated);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoría no encontrada con id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponseDTO updateImage(Long id, MultipartFile file) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));

        String fileName = localStorageService.store(file);

        category.setImagePath(fileName);

        Category updated = categoryRepository.save(category);
        return toResponseDTO(updated);
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setType(category.getType());

        String imagePath = category.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                // Obtener la imagen en formato dataURL
                String imageDataUrl = localStorageService.getImage(imagePath);

                dto.setImagePath(imageDataUrl);
            } catch (Exception e) {
                // Si hay un error, simplemente dejamos la ruta normal
                dto.setImagePath(imagePath);
            }
        } else {
            dto.setImagePath(null);
        }

        return dto;
    }
}