package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.forumviajeros.backend.model.Category;

/**
 * Tests de integración para CategoryRepository con PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("Europa");
        testCategory.setDescription("Foros sobre Europa");
        testCategory.setType("CONTINENT");
    }

    @Test
    @DisplayName("Debe guardar una categoría en la base de datos")
    void shouldSaveCategory() {
        Category saved = categoryRepository.save(testCategory);
        
        assertNotNull(saved.getId(), "Categoría debe tener ID generado");
        assertEquals("Europa", saved.getName());
        assertEquals("CONTINENT", saved.getType());
    }

    @Test
    @DisplayName("Debe encontrar categoría por nombre")
    void shouldFindCategoryByName() {
        categoryRepository.save(testCategory);
        
        Optional<Category> found = categoryRepository.findByName("Europa");
        
        assertTrue(found.isPresent(), "Categoría debe existir");
        assertEquals("Europa", found.get().getName());
    }

    @Test
    @DisplayName("Debe encontrar categorías por tipo")
    void shouldFindCategoriesByType() {
        categoryRepository.save(testCategory);
        
        Category asia = new Category();
        asia.setName("Asia");
        asia.setDescription("Foros sobre Asia");
        asia.setType("CONTINENT");
        categoryRepository.save(asia);
        
        List<Category> continents = categoryRepository.findByType("CONTINENT");
        
        assertTrue(continents.size() >= 2, "Debe encontrar al menos 2 continentes");
    }

    @Test
    @DisplayName("Debe verificar si nombre existe")
    void shouldCheckNameExists() {
        categoryRepository.save(testCategory);
        
        assertTrue(categoryRepository.existsByName("Europa"), "Nombre debe existir");
        assertFalse(categoryRepository.existsByName("Inexistente"), "Nombre no debe existir");
    }

    @Test
    @DisplayName("Debe actualizar categoría")
    void shouldUpdateCategory() {
        Category saved = categoryRepository.save(testCategory);
        saved.setDescription("Descripción actualizada");
        
        Category updated = categoryRepository.save(saved);
        
        assertEquals("Descripción actualizada", updated.getDescription());
    }

    @Test
    @DisplayName("Debe eliminar categoría")
    void shouldDeleteCategory() {
        Category saved = categoryRepository.save(testCategory);
        Long id = saved.getId();
        
        categoryRepository.delete(saved);
        
        assertFalse(categoryRepository.findById(id).isPresent(), "Categoría debe estar eliminada");
    }

    @Test
    @DisplayName("Debe mantener unicidad de nombre")
    void shouldEnforceNameUniqueness() {
        categoryRepository.save(testCategory);
        
        Category duplicate = new Category();
        duplicate.setName("Europa"); // Mismo nombre
        duplicate.setDescription("Otra descripción");
        duplicate.setType("CONTINENT");
        
        assertThrows(Exception.class, () -> {
            categoryRepository.save(duplicate);
            categoryRepository.flush();
        }, "Debe lanzar excepción por nombre duplicado");
    }
}

