package com.forumviajeros.backend.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.category.CategoryRequestDTO;
import com.forumviajeros.backend.dto.category.CategoryResponseDTO;
import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.repository.CategoryRepository;
import com.forumviajeros.backend.service.storage.LocalStorageService;
import com.forumviajeros.backend.service.storage.StorageException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocalStorageService localStorageService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar categoría de prueba
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Europa");
        testCategory.setDescription("Foros sobre países europeos");
        testCategory.setType("CONTINENT");

        // Configurar DTO de prueba
        categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Asia");
        categoryRequestDTO.setDescription("Foros sobre países asiáticos");
        categoryRequestDTO.setType("CONTINENT");
    }

    @Test
    @DisplayName("Obtener todas las categorías")
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));

        // Act
        List<CategoryResponseDTO> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Europa", result.get(0).getName());
    }

    @Test
    @DisplayName("Obtener categoría por ID exitosamente")
    void findById_ShouldReturnCategory_WhenExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        CategoryResponseDTO result = categoryService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Europa", result.getName());
    }

    @Test
    @DisplayName("Obtener categoría por ID falla cuando no existe")
    void findById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findById(1L);
        });
    }

    @Test
    @DisplayName("Crear categoría exitosamente")
    void create_ShouldSucceed_WhenValidData() {
        // Arrange
        when(categoryRepository.existsByName("Asia")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(2L);
            return category;
        });

        // Act
        CategoryResponseDTO result = categoryService.create(categoryRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Asia", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Crear categoría falla cuando el nombre ya existe")
    void create_ShouldThrowException_WhenNameExists() {
        // Arrange
        when(categoryRepository.existsByName("Asia")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.create(categoryRequestDTO);
        });
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Actualizar categoría exitosamente")
    void update_ShouldSucceed_WhenValidData() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Asia")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryResponseDTO result = categoryService.update(1L, categoryRequestDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Actualizar categoría falla cuando no existe")
    void update_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.update(1L, categoryRequestDTO);
        });
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Eliminar categoría exitosamente")
    void delete_ShouldSucceed_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        categoryService.delete(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar categoría falla cuando no existe")
    void delete_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.delete(1L);
        });
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Subir imagen de categoría exitosamente")
    void updateImage_ShouldSucceed_WhenValidFile() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(localStorageService.store(file)).thenReturn("stored-image.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryResponseDTO result = categoryService.updateImage(1L, file);

        // Assert
        assertNotNull(result);
        verify(localStorageService).store(file);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Subir imagen falla cuando la categoría no existe")
    void updateImage_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.updateImage(1L, file);
        });
        verify(localStorageService, never()).store(any());
    }

    @Test
    @DisplayName("Subir imagen falla cuando hay error de almacenamiento")
    void updateImage_ShouldThrowException_WhenStorageFails() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(localStorageService.store(file)).thenThrow(new StorageException("Error de almacenamiento"));

        // Act & Assert
        assertThrows(StorageException.class, () -> {
            categoryService.updateImage(1L, file);
        });
    }
}

