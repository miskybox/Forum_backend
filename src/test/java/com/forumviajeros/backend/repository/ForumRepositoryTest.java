package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.User;

/**
 * Tests de integración para ForumRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ForumRepositoryTest {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Category testCategory;
    private Forum testForum;

    @BeforeEach
    void setUp() {
        // Crear usuario
        testUser = new User();
        testUser.setUsername("forumuser_" + System.currentTimeMillis());
        testUser.setEmail("forum" + System.currentTimeMillis() + "@example.com");
        testUser.setPassword("password123");
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser = userRepository.save(testUser);

        // Crear categoría
        testCategory = new Category();
        testCategory.setName("TestCategory_" + System.currentTimeMillis());
        testCategory.setDescription("Test category for forum tests");
        testCategory.setType("CONTINENT");
        testCategory = categoryRepository.save(testCategory);

        // Crear foro
        testForum = new Forum();
        testForum.setTitle("Viaje a París");
        testForum.setDescription("Comparte tu experiencia en París");
        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        testForum.setUser(testUser);
        testForum.setCategory(testCategory);
        testForum.setViewCount(0L);
    }

    @Test
    @DisplayName("Debe guardar un foro en la base de datos")
    void shouldSaveForum() {
        Forum saved = forumRepository.save(testForum);
        
        assertNotNull(saved.getId(), "Foro debe tener ID generado");
        assertEquals("Viaje a París", saved.getTitle());
        assertEquals(testUser.getId(), saved.getUser().getId());
        assertEquals(testCategory.getId(), saved.getCategory().getId());
    }

    @Test
    @DisplayName("Debe encontrar foros por usuario")
    void shouldFindForumsByUser() {
        forumRepository.save(testForum);
        
        List<Forum> forums = forumRepository.findByUser(testUser);
        
        assertFalse(forums.isEmpty(), "Debe encontrar foros del usuario");
        assertEquals("Viaje a París", forums.get(0).getTitle());
    }

    @Test
    @DisplayName("Debe encontrar foros por categoría")
    void shouldFindForumsByCategory() {
        forumRepository.save(testForum);
        
        List<Forum> forums = forumRepository.findByCategory(testCategory);
        
        assertFalse(forums.isEmpty(), "Debe encontrar foros de la categoría");
        assertEquals("Viaje a París", forums.get(0).getTitle());
    }

    @Test
    @DisplayName("Debe buscar foros por keyword")
    void shouldSearchForumsByKeyword() {
        forumRepository.save(testForum);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Forum> results = forumRepository.searchByKeyword("París", pageable);
        
        assertFalse(results.isEmpty(), "Debe encontrar foros con la keyword");
        assertTrue(results.getContent().get(0).getTitle().contains("París") || 
                   results.getContent().get(0).getDescription().contains("París"));
    }

    @Test
    @DisplayName("Debe paginar foros por categoría")
    void shouldPaginateForumsByCategory() {
        // Crear múltiples foros
        for (int i = 0; i < 15; i++) {
            Forum forum = new Forum();
            forum.setTitle("Foro " + i);
            forum.setDescription("Descripción " + i);
            forum.setStatus(Forum.ForumStatus.ACTIVE);
            forum.setUser(testUser);
            forum.setCategory(testCategory);
            forum.setViewCount(0L);
            forumRepository.save(forum);
        }
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Forum> page = forumRepository.findByCategory(testCategory, pageable);
        
        assertEquals(10, page.getContent().size(), "Debe retornar 10 foros por página");
        assertTrue(page.getTotalElements() >= 15, "Debe tener al menos 15 foros en total");
    }

    @Test
    @DisplayName("Debe filtrar foros por categoría y estado")
    void shouldFilterForumsByCategoryAndStatus() {
        // Crear foro activo
        Forum activeForum = new Forum();
        activeForum.setTitle("Foro Activo");
        activeForum.setDescription("Descripción");
        activeForum.setStatus(Forum.ForumStatus.ACTIVE);
        activeForum.setUser(testUser);
        activeForum.setCategory(testCategory);
        activeForum.setViewCount(0L);
        forumRepository.save(activeForum);

        // Crear foro inactivo
        Forum inactiveForum = new Forum();
        inactiveForum.setTitle("Foro Inactivo");
        inactiveForum.setDescription("Descripción");
        inactiveForum.setStatus(Forum.ForumStatus.INACTIVE);
        inactiveForum.setUser(testUser);
        inactiveForum.setCategory(testCategory);
        inactiveForum.setViewCount(0L);
        forumRepository.save(inactiveForum);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Forum> activeForums = forumRepository.findByCategoryAndStatus(
                testCategory, Forum.ForumStatus.ACTIVE, pageable);

        assertFalse(activeForums.isEmpty(), "Debe encontrar foros activos");
        activeForums.getContent().forEach(forum -> 
            assertEquals(Forum.ForumStatus.ACTIVE, forum.getStatus())
        );
    }

    @Test
    @DisplayName("Debe actualizar foro")
    void shouldUpdateForum() {
        Forum saved = forumRepository.save(testForum);
        saved.setTitle("Viaje Actualizado");
        saved.setDescription("Nueva descripción");
        
        Forum updated = forumRepository.save(saved);
        
        assertEquals("Viaje Actualizado", updated.getTitle());
        assertEquals("Nueva descripción", updated.getDescription());
    }

    @Test
    @DisplayName("Debe eliminar foro")
    void shouldDeleteForum() {
        Forum saved = forumRepository.save(testForum);
        Long id = saved.getId();
        
        forumRepository.delete(saved);
        
        assertFalse(forumRepository.findById(id).isPresent(), "Foro debe estar eliminado");
    }

    @Test
    @DisplayName("Debe mantener relación con usuario")
    void shouldMaintainUserRelationship() {
        Forum saved = forumRepository.save(testForum);
        
        Forum found = forumRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getUser(), "Foro debe tener usuario");
        assertEquals(testUser.getId(), found.getUser().getId());
    }

    @Test
    @DisplayName("Debe mantener relación con categoría")
    void shouldMaintainCategoryRelationship() {
        Forum saved = forumRepository.save(testForum);
        
        Forum found = forumRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getCategory(), "Foro debe tener categoría");
        assertEquals(testCategory.getId(), found.getCategory().getId());
    }
}

