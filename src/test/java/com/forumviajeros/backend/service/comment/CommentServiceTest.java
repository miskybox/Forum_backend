package com.forumviajeros.backend.service.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.forumviajeros.backend.dto.comment.CommentRequestDTO;
import com.forumviajeros.backend.dto.comment.CommentResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Comment;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.CommentRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CommentRequestDTO commentRequestDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Configurar post de prueba
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Post de Prueba");
        testPost.setUser(testUser);

        // Configurar comentario de prueba
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("Comentario de prueba");
        testComment.setUser(testUser);
        testComment.setPost(testPost);
        testComment.setStatus(Comment.CommentStatus.ACTIVE);
        testComment.setCreatedAt(LocalDateTime.now());

        // Configurar DTO de prueba
        commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setContent("Nuevo comentario");

        // Configurar autenticación de prueba
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testuser", null, authorities);
    }

    @Test
    @DisplayName("Crear comentario exitosamente")
    void createComment_ShouldSucceed_WhenValidData() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        // Act
        CommentResponseDTO result = commentService.createComment(commentRequestDTO, authentication, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo comentario", result.getContent());
        verify(commentRepository).save(any(Comment.class));
        verify(userRepository).findByUsername("testuser");
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("Crear comentario falla cuando el usuario no existe")
    void createComment_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            commentService.createComment(commentRequestDTO, authentication, 1L);
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Crear comentario falla cuando el post no existe")
    void createComment_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(commentRequestDTO, authentication, 1L);
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Obtener comentario por ID exitosamente")
    void getComment_ShouldReturnComment_WhenExists() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act
        CommentResponseDTO result = commentService.getComment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Comentario de prueba", result.getContent());
    }

    @Test
    @DisplayName("Obtener comentario por ID falla cuando no existe")
    void getComment_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getComment(1L);
        });
    }

    @Test
    @DisplayName("Obtener todos los comentarios")
    void getAllComments_ShouldReturnAllComments() {
        // Arrange
        when(commentRepository.findAll()).thenReturn(List.of(testComment));

        // Act
        List<CommentResponseDTO> result = commentService.getAllComments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findAll();
    }

    @Test
    @DisplayName("Obtener comentarios por post")
    void getCommentsByPost_ShouldReturnComments_WhenPostExists() {
        // Arrange
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostId(1L)).thenReturn(List.of(testComment));

        // Act
        List<CommentResponseDTO> result = commentService.getCommentsByPost(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByPostId(1L);
    }

    @Test
    @DisplayName("Obtener comentarios por post falla cuando el post no existe")
    void getCommentsByPost_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        when(postRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentsByPost(1L);
        });
        verify(commentRepository, never()).findByPostId(any());
    }

    @Test
    @DisplayName("Actualizar comentario exitosamente")
    void updateComment_ShouldSucceed_WhenValidData() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        CommentResponseDTO result = commentService.updateComment(1L, commentRequestDTO, authentication);

        // Assert
        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Actualizar comentario falla cuando no existe")
    void updateComment_ShouldThrowException_WhenNotFound() {
        // Arrange
        lenient().when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(1L, commentRequestDTO, authentication);
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Eliminar comentario exitosamente por el propietario")
    void deleteComment_ShouldSucceed_WhenOwner() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        commentService.deleteComment(1L, authentication);

        // Assert
        verify(commentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comentario exitosamente por admin")
    void deleteComment_ShouldSucceed_WhenAdmin() {
        // Arrange
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication adminAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "admin", null, adminAuthorities);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // Act
        commentService.deleteComment(1L, adminAuth);

        // Assert
        verify(commentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comentario falla cuando no es el propietario ni admin")
    void deleteComment_ShouldThrowException_WhenNotOwnerOrAdmin() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        
        List<GrantedAuthority> userAuthorities = new ArrayList<>();
        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication otherAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "otheruser", null, userAuthorities);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            commentService.deleteComment(1L, otherAuth);
        });
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar comentario falla cuando no existe")
    void deleteComment_ShouldThrowException_WhenNotFound() {
        // Arrange
        lenient().when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(1L, authentication);
        });
        verify(commentRepository, never()).deleteById(any());
    }
}

