package com.forumviajeros.backend.service.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.forumviajeros.backend.dto.post.PostRequestDTO;
import com.forumviajeros.backend.dto.post.PostResponseDTO;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.ForumRepository;
import com.forumviajeros.backend.repository.ImageRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.TagRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.storage.LocalStorageService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private LocalStorageService localStorageService;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private Forum testForum;
    private Post testPost;
    private PostRequestDTO postRequestDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Configurar foro de prueba
        testForum = new Forum();
        testForum.setId(1L);
        testForum.setTitle("Foro de Prueba");

        // Configurar post de prueba
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Post de Prueba");
        testPost.setContent("Contenido del post de prueba");
        testPost.setUser(testUser);
        testPost.setForum(testForum);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setStatus(Post.PostStatus.ACTIVE);
        testPost.setViewCount(0L);

        // Configurar DTO de prueba
        postRequestDTO = new PostRequestDTO();
        postRequestDTO.setTitle("Nuevo Post");
        postRequestDTO.setContent("Contenido del nuevo post");
        postRequestDTO.setForumId(1L);
        postRequestDTO.setStatus("ACTIVE");

        // Configurar autenticación de prueba
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testuser", null, authorities);
    }

    @Test
    @DisplayName("Crear post exitosamente")
    void createPost_ShouldSucceed_WhenValidData() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L);
            return post;
        });

        // Act
        PostResponseDTO result = postService.createPost(postRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo Post", result.getTitle());
        verify(postRepository).save(any(Post.class));
        verify(userRepository).findById(1L);
        verify(forumRepository).findById(1L);
    }

    @Test
    @DisplayName("Crear post falla cuando el usuario no existe")
    void createPost_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            postService.createPost(postRequestDTO, 1L);
        });
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Crear post falla cuando el foro no existe")
    void createPost_ShouldThrowException_WhenForumNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(forumRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            postService.createPost(postRequestDTO, 1L);
        });
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Obtener post por ID exitosamente")
    void findById_ShouldReturnPost_WhenExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        PostResponseDTO result = postService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Post de Prueba", result.getTitle());
    }

    @Test
    @DisplayName("Obtener post por ID falla cuando no existe")
    void findById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            postService.findById(1L);
        });
    }

    @Test
    @DisplayName("Obtener todos los posts paginados")
    void findAll_ShouldReturnPage_WhenPostsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(testPost);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findAll(pageable)).thenReturn(postPage);

        // Act
        Page<PostResponseDTO> result = postService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Obtener posts por foro")
    void findByForum_ShouldReturnPosts_WhenForumExists() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(postRepository.findByForum(testForum)).thenReturn(List.of(testPost));

        // Act
        List<PostResponseDTO> result = postService.findByForum(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository).findByForum(testForum);
        verify(forumRepository).findById(1L);
    }

    @Test
    @DisplayName("Actualizar post exitosamente")
    void updatePost_ShouldSucceed_WhenValidData() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        // authentication ya está configurado en setUp() con username "testuser"
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostResponseDTO result = postService.updatePost(1L, postRequestDTO, authentication);

        // Assert
        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Eliminar post exitosamente")
    void delete_ShouldSucceed_WhenPostExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        // authentication ya está configurado en setUp() con username "testuser"

        // Act
        postService.delete(1L, authentication);

        // Assert
        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("Obtener ID de usuario por username")
    void getUserIdByUsername_ShouldReturnUserId_WhenUserExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Long userId = postService.getUserIdByUsername("testuser");

        // Assert
        assertNotNull(userId);
        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Obtener ID de usuario falla cuando no existe")
    void getUserIdByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            postService.getUserIdByUsername("nonexistent");
        });
    }

    @Test
    @DisplayName("No se puede crear post en foro inactivo")
    void createPost_ShouldFail_WhenForumInactive() {
        // Arrange
        testForum.setStatus(Forum.ForumStatus.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            postService.createPost(postRequestDTO, 1L)
        );
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("No se puede crear post en foro archivado")
    void createPost_ShouldFail_WhenForumArchived() {
        // Arrange
        testForum.setStatus(Forum.ForumStatus.ARCHIVED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            postService.createPost(postRequestDTO, 1L)
        );
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Se puede crear post en foro activo")
    void createPost_ShouldSucceed_WhenForumActive() {
        // Arrange
        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostResponseDTO result = postService.createPost(postRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }
}

