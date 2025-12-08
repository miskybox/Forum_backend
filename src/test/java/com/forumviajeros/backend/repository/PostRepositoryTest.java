package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Import;


import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;

/**
 * Tests de integración para PostRepository con PostgreSQL
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ForumRepository forumRepository;

    private User testUser;
    private Category testCategory;
    private Forum testForum;
    private Post testPost;

    @BeforeEach
    void setUp() {
        // Crear usuario
        testUser = new User();
        testUser.setUsername("postuser");
        testUser.setEmail("post@example.com");
        testUser.setPassword("password123");
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser = userRepository.save(testUser);

        // Crear categoría
        testCategory = new Category();
        testCategory.setName("Europa");
        testCategory.setDescription("Foros sobre Europa");
        testCategory.setType("CONTINENT");
        testCategory = categoryRepository.save(testCategory);

        // Crear foro
        testForum = new Forum();
        testForum.setTitle("Foro de Prueba");
        testForum.setDescription("Descripción del foro");
        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        testForum.setUser(testUser);
        testForum.setCategory(testCategory);
        testForum.setViewCount(0L);
        testForum = forumRepository.save(testForum);

        // Crear post
        testPost = new Post();
        testPost.setTitle("Mi experiencia en París");
        testPost.setContent("Fue una experiencia increíble visitar París...");
        testPost.setStatus(Post.PostStatus.ACTIVE);
        testPost.setForum(testForum);
        testPost.setUser(testUser);
        testPost.setViewCount(0L);
    }

    @Test
    @DisplayName("Debe guardar un post en la base de datos")
    void shouldSavePost() {
        Post saved = postRepository.save(testPost);
        
        assertNotNull(saved.getId(), "Post debe tener ID generado");
        assertEquals("Mi experiencia en París", saved.getTitle());
        assertEquals(testForum.getId(), saved.getForum().getId());
        assertEquals(testUser.getId(), saved.getUser().getId());
    }

    @Test
    @DisplayName("Debe encontrar posts por foro")
    void shouldFindPostsByForum() {
        postRepository.save(testPost);
        
        List<Post> posts = postRepository.findByForum(testForum);
        
        assertFalse(posts.isEmpty(), "Debe encontrar posts del foro");
        assertEquals("Mi experiencia en París", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("Debe encontrar posts por usuario")
    void shouldFindPostsByUser() {
        postRepository.save(testPost);
        
        List<Post> posts = postRepository.findByUser(testUser);
        
        assertFalse(posts.isEmpty(), "Debe encontrar posts del usuario");
        assertEquals("Mi experiencia en París", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("Debe actualizar post")
    void shouldUpdatePost() {
        Post saved = postRepository.save(testPost);
        saved.setTitle("Título Actualizado");
        saved.setContent("Contenido actualizado");
        
        Post updated = postRepository.save(saved);
        
        assertEquals("Título Actualizado", updated.getTitle());
        assertEquals("Contenido actualizado", updated.getContent());
    }

    @Test
    @DisplayName("Debe eliminar post")
    void shouldDeletePost() {
        Post saved = postRepository.save(testPost);
        Long id = saved.getId();
        
        postRepository.delete(saved);
        
        assertFalse(postRepository.findById(id).isPresent(), "Post debe estar eliminado");
    }

    @Test
    @DisplayName("Debe mantener relación con foro")
    void shouldMaintainForumRelationship() {
        Post saved = postRepository.save(testPost);
        
        Post found = postRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getForum(), "Post debe tener foro");
        assertEquals(testForum.getId(), found.getForum().getId());
    }

    @Test
    @DisplayName("Debe mantener relación con usuario")
    void shouldMaintainUserRelationship() {
        Post saved = postRepository.save(testPost);
        
        Post found = postRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getUser(), "Post debe tener usuario");
        assertEquals(testUser.getId(), found.getUser().getId());
    }

    @Test
    @DisplayName("Debe incrementar viewCount")
    void shouldIncrementViewCount() {
        Post saved = postRepository.save(testPost);
        assertEquals(0L, saved.getViewCount());
        
        saved.setViewCount(saved.getViewCount() + 1);
        Post updated = postRepository.save(saved);
        
        assertEquals(1L, updated.getViewCount());
    }
}

