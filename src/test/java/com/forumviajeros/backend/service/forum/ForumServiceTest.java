package com.forumviajeros.backend.service.forum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.forum.ForumRequestDTO;
import com.forumviajeros.backend.dto.forum.ForumResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.CategoryRepository;
import com.forumviajeros.backend.repository.ForumRepository;
import com.forumviajeros.backend.repository.TagRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.storage.LocalStorageService;
import com.forumviajeros.backend.service.storage.StorageException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumService Tests")
class ForumServiceTest {

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private LocalStorageService localStorageService;

    @InjectMocks
    private ForumServiceImpl forumService;

    private User testUser;
    private Category testCategory;
    private Forum testForum;
    private ForumRequestDTO forumRequestDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(new HashSet<>());

        // Configurar categoría de prueba
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Europa");
        testCategory.setDescription("Foros sobre países europeos");
        testCategory.setType("CONTINENT");

        // Configurar foro de prueba
        testForum = new Forum();
        testForum.setId(1L);
        testForum.setTitle("Foro de Prueba");
        testForum.setDescription("Descripción del foro de prueba");
        testForum.setCategory(testCategory);
        testForum.setUser(testUser);
        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        testForum.setCreatedAt(LocalDateTime.now());
        testForum.setViewCount(0L);
        testForum.setPosts(new ArrayList<>());
        testForum.setTags(new ArrayList<>());

        // Configurar DTO de prueba
        forumRequestDTO = new ForumRequestDTO();
        forumRequestDTO.setTitle("Nuevo Foro");
        forumRequestDTO.setDescription("Descripción del nuevo foro");
        forumRequestDTO.setCategoryId(1L);
        forumRequestDTO.setTags(List.of("viajes", "europa"));

        // Configurar autenticación de prueba
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testuser", null, authorities);
    }

    @Test
    @DisplayName("Crear foro exitosamente")
    void createForum_ShouldSucceed_WhenValidData() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(tagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(forumRepository.save(any(Forum.class))).thenAnswer(invocation -> {
            Forum forum = invocation.getArgument(0);
            forum.setId(1L);
            return forum;
        });

        // Act
        ForumResponseDTO result = forumService.createForum(forumRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo Foro", result.getTitle());
        verify(forumRepository).save(any(Forum.class));
        verify(categoryRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Crear foro falla cuando el usuario no existe")
    void createForum_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.createForum(forumRequestDTO, 1L);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }

    @Test
    @DisplayName("Crear foro falla cuando la categoría no existe")
    void createForum_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.createForum(forumRequestDTO, 1L);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }

    @Test
    @DisplayName("Obtener foro por ID exitosamente")
    void findById_ShouldReturnForum_WhenExists() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));

        // Act
        ForumResponseDTO result = forumService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Foro de Prueba", result.getTitle());
    }

    @Test
    @DisplayName("Obtener foro por ID falla cuando no existe")
    void findById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.findById(1L);
        });
    }

    @Test
    @DisplayName("Obtener todos los foros paginados")
    void findAll_ShouldReturnPage_WhenForumsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Forum> forums = List.of(testForum);
        Page<Forum> forumPage = new PageImpl<>(forums, pageable, 1);
        when(forumRepository.findAll(pageable)).thenReturn(forumPage);

        // Act
        Page<ForumResponseDTO> result = forumService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Buscar foros por palabra clave")
    void searchByKeyword_ShouldReturnForums_WhenKeywordMatches() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Forum> forumPage = new PageImpl<>(List.of(testForum), pageable, 1);
        when(forumRepository.searchByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(forumPage);

        // Act
        List<ForumResponseDTO> result = forumService.searchByKeyword("prueba");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(forumRepository).searchByKeyword(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Actualizar foro exitosamente")
    void updateForum_ShouldSucceed_WhenValidData() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(tagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(forumRepository.save(any(Forum.class))).thenReturn(testForum);

        // Act
        ForumResponseDTO result = forumService.updateForum(1L, forumRequestDTO, authentication);

        // Assert
        assertNotNull(result);
        verify(forumRepository).save(any(Forum.class));
    }

    @Test
    @DisplayName("Actualizar foro falla cuando no existe")
    void updateForum_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.updateForum(1L, forumRequestDTO, authentication);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }

    @Test
    @DisplayName("Eliminar foro exitosamente")
    void delete_ShouldSucceed_WhenForumExists() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(authentication.getName()).thenReturn("testuser");

        // Act
        forumService.delete(1L, authentication);

        // Assert
        verify(forumRepository).delete(testForum);
    }

    @Test
    @DisplayName("Eliminar foro falla cuando no existe")
    void delete_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(forumRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.delete(1L, authentication);
        });
        verify(forumRepository, never()).delete(any(Forum.class));
    }

    @Test
    @DisplayName("Subir imagen de foro exitosamente")
    void updateImage_ShouldSucceed_WhenValidFile() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(localStorageService.store(file)).thenReturn("stored-file.jpg");
        when(forumRepository.save(any(Forum.class))).thenReturn(testForum);

        // Act
        ForumResponseDTO result = forumService.updateImage(1L, file, authentication);

        // Assert
        assertNotNull(result);
        verify(localStorageService).store(file);
        verify(forumRepository).save(any(Forum.class));
    }

    @Test
    @DisplayName("Subir imagen falla cuando el archivo está vacío")
    void updateImage_ShouldThrowException_WhenFileEmpty() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            forumService.updateImage(1L, file, authentication);
        });
        verify(localStorageService, never()).store(any());
    }

    @Test
    @DisplayName("Subir imagen falla cuando el foro no existe")
    void updateImage_ShouldThrowException_WhenForumNotFound() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(forumRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.updateImage(1L, file, authentication);
        });
        verify(localStorageService, never()).store(any());
    }

    @Test
    @DisplayName("Subir imagen falla cuando hay error de almacenamiento")
    void updateImage_ShouldThrowException_WhenStorageFails() {
        // Arrange
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(file.isEmpty()).thenReturn(false);
        when(localStorageService.store(file)).thenThrow(new StorageException("Error de almacenamiento"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            forumService.updateImage(1L, file, authentication);
        });
    }

    @Test
    @DisplayName("Actualizar estado del foro como moderador")
    void updateForumStatus_ShouldSucceed_WhenModerator() {
        // Arrange
        Authentication modAuth = org.mockito.Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        lenient().when(modAuth.getAuthorities()).thenReturn((List) authorities);

        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(forumRepository.save(any(Forum.class))).thenReturn(testForum);

        // Act
        ForumResponseDTO result = forumService.updateForumStatus(1L, "INACTIVE", modAuth);

        // Assert
        assertNotNull(result);
        verify(forumRepository).save(any(Forum.class));
    }

    @Test
    @DisplayName("Actualizar estado del foro como admin")
    void updateForumStatus_ShouldSucceed_WhenAdmin() {
        // Arrange
        Authentication adminAuth = org.mockito.Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        lenient().when(adminAuth.getAuthorities()).thenReturn((List) authorities);

        testForum.setStatus(Forum.ForumStatus.ACTIVE);
        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));
        when(forumRepository.save(any(Forum.class))).thenReturn(testForum);

        // Act
        ForumResponseDTO result = forumService.updateForumStatus(1L, "ARCHIVED", adminAuth);

        // Assert
        assertNotNull(result);
        verify(forumRepository).save(any(Forum.class));
    }

    @Test
    @DisplayName("Actualizar estado del foro falla cuando usuario no es admin/moderador")
    void updateForumStatus_ShouldFail_WhenNotAdminOrModerator() {
        // Arrange
        Authentication userAuth = org.mockito.Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        lenient().when(userAuth.getAuthorities()).thenReturn((List) authorities);

        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));

        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            forumService.updateForumStatus(1L, "INACTIVE", userAuth);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }

    @Test
    @DisplayName("Actualizar estado del foro falla cuando estado inválido")
    void updateForumStatus_ShouldFail_WhenInvalidStatus() {
        // Arrange
        Authentication adminAuth = org.mockito.Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        lenient().when(adminAuth.getAuthorities()).thenReturn((List) authorities);

        when(forumRepository.findById(1L)).thenReturn(Optional.of(testForum));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            forumService.updateForumStatus(1L, "INVALID_STATUS", adminAuth);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }

    @Test
    @DisplayName("Actualizar estado del foro falla cuando foro no existe")
    void updateForumStatus_ShouldFail_WhenForumNotFound() {
        // Arrange
        Authentication adminAuth = org.mockito.Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        lenient().when(adminAuth.getAuthorities()).thenReturn((List) authorities);

        when(forumRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            forumService.updateForumStatus(99L, "INACTIVE", adminAuth);
        });
        verify(forumRepository, never()).save(any(Forum.class));
    }
}

