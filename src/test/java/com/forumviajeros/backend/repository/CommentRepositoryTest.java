package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Comment;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;

/**
 * Tests de integración para CommentRepository con PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private PostRepository postRepository;

    private User testUser;
    private Category testCategory;
    private Forum testForum;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        // Crear usuario
        testUser = new User();
        testUser.setUsername("commentuser");
        testUser.setEmail("comment@example.com");
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
        testForum.setDescription("Descripción");
        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        testForum.setUser(testUser);
        testForum.setCategory(testCategory);
        testForum.setViewCount(0L);
        testForum = forumRepository.save(testForum);

        // Crear post
        testPost = new Post();
        testPost.setTitle("Post de Prueba");
        testPost.setContent("Contenido del post");
        testPost.setStatus(Post.PostStatus.ACTIVE);
        testPost.setForum(testForum);
        testPost.setUser(testUser);
        testPost.setViewCount(0L);
        testPost = postRepository.save(testPost);

        // Crear comentario
        testComment = new Comment();
        testComment.setContent("Este es un comentario de prueba");
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setStatus(Comment.CommentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Debe guardar un comentario en la base de datos")
    void shouldSaveComment() {
        Comment saved = commentRepository.save(testComment);
        
        assertNotNull(saved.getId(), "Comentario debe tener ID generado");
        assertEquals("Este es un comentario de prueba", saved.getContent());
        assertEquals(testPost.getId(), saved.getPost().getId());
        assertEquals(testUser.getId(), saved.getUser().getId());
    }

    @Test
    @DisplayName("Debe encontrar comentarios por post")
    void shouldFindCommentsByPost() {
        commentRepository.save(testComment);
        
        List<Comment> comments = commentRepository.findByPost(testPost);
        
        assertFalse(comments.isEmpty(), "Debe encontrar comentarios del post");
        assertEquals("Este es un comentario de prueba", comments.get(0).getContent());
    }

    @Test
    @DisplayName("Debe actualizar comentario")
    void shouldUpdateComment() {
        Comment saved = commentRepository.save(testComment);
        saved.setContent("Contenido actualizado");
        
        Comment updated = commentRepository.save(saved);
        
        assertEquals("Contenido actualizado", updated.getContent());
    }

    @Test
    @DisplayName("Debe eliminar comentario")
    void shouldDeleteComment() {
        Comment saved = commentRepository.save(testComment);
        Long id = saved.getId();
        
        commentRepository.delete(saved);
        
        assertFalse(commentRepository.findById(id).isPresent(), "Comentario debe estar eliminado");
    }

    @Test
    @DisplayName("Debe mantener relación con post")
    void shouldMaintainPostRelationship() {
        Comment saved = commentRepository.save(testComment);
        
        Comment found = commentRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getPost(), "Comentario debe tener post");
        assertEquals(testPost.getId(), found.getPost().getId());
    }

    @Test
    @DisplayName("Debe mantener relación con usuario")
    void shouldMaintainUserRelationship() {
        Comment saved = commentRepository.save(testComment);
        
        Comment found = commentRepository.findById(saved.getId()).orElseThrow();
        
        assertNotNull(found.getUser(), "Comentario debe tener usuario");
        assertEquals(testUser.getId(), found.getUser().getId());
    }
}

