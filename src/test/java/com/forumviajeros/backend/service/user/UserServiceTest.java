package com.forumviajeros.backend.service.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.model.User.UserStatus;
import com.forumviajeros.backend.repository.RoleRepository;
import com.forumviajeros.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User adminUser;
    private Role userRole;
    private Role adminRole;
    private Role moderatorRole;
    private Authentication adminAuth;
    private Authentication moderatorAuth;
    @SuppressWarnings("unused")
    private Authentication userAuth;

    @BeforeEach
    void setUp() {
        // Configurar roles
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        moderatorRole = new Role();
        moderatorRole.setId(3L);
        moderatorRole.setName("ROLE_MODERATOR");

        // Configurar usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus(UserStatus.ACTIVE);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        testUser.setRoles(userRoles);

        // Configurar admin
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setStatus(UserStatus.ACTIVE);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);

        // Initialize auth mocks without when() statements
        adminAuth = org.mockito.Mockito.mock(Authentication.class);
        moderatorAuth = org.mockito.Mockito.mock(Authentication.class);
        userAuth = org.mockito.Mockito.mock(Authentication.class);
    }

    @Test
    @DisplayName("Moderador puede banear usuario regular")
    void updateUserStatus_ModeratorCanBanUser() {
        // Arrange
        List<GrantedAuthority> modAuthorities = new ArrayList<>();
        modAuthorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) modAuthorities;
        lenient().when(moderatorAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "BANNED", moderatorAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Admin puede banear usuario regular")
    void updateUserStatus_AdminCanBanUser() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "BANNED", adminAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Admin puede cambiar estado de otro admin")
    void updateUserStatus_AdminCanBanAdmin() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(2L, "INACTIVE", adminAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Moderador NO puede banear admin")
    void updateUserStatus_ModeratorCannotBanAdmin() {
        // Arrange
        List<GrantedAuthority> modAuthorities = new ArrayList<>();
        modAuthorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) modAuthorities;
        lenient().when(moderatorAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            userService.updateUserStatus(2L, "BANNED", moderatorAuth)
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Falla con estado inválido")
    void updateUserStatus_FailsWithInvalidStatus() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUserStatus(1L, "INVALID_STATUS", adminAuth)
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Falla cuando usuario no existe")
    void updateUserStatus_FailsWhenUserNotFound() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            userService.updateUserStatus(99L, "BANNED", adminAuth)
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Puede cambiar a ACTIVE")
    void updateUserStatus_CanSetToActive() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        testUser.setStatus(UserStatus.BANNED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "ACTIVE", adminAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Puede cambiar a INACTIVE")
    void updateUserStatus_CanSetToInactive() {
        // Arrange
        List<GrantedAuthority> modAuthorities = new ArrayList<>();
        modAuthorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) modAuthorities;
        lenient().when(moderatorAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "INACTIVE", moderatorAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Puede cambiar a DELETED")
    void updateUserStatus_CanSetToDeleted() {
        // Arrange
        List<GrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) adminAuthorities;
        lenient().when(adminAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "DELETED", adminAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Acepta estado en minúsculas")
    void updateUserStatus_AcceptsLowercaseStatus() {
        // Arrange
        List<GrantedAuthority> modAuthorities = new ArrayList<>();
        modAuthorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) modAuthorities;
        lenient().when(moderatorAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.updateUserStatus(1L, "banned", moderatorAuth);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Moderador NO puede banear otro moderador")
    void updateUserStatus_ModeratorCannotBanModerator() {
        // Arrange
        User moderatorUser = new User();
        moderatorUser.setId(3L);
        moderatorUser.setUsername("moderator");
        moderatorUser.setEmail("mod@example.com");
        moderatorUser.setStatus(UserStatus.ACTIVE);
        Set<Role> modRoles = new HashSet<>();
        modRoles.add(moderatorRole);
        moderatorUser.setRoles(modRoles);

        List<GrantedAuthority> modAuthorities = new ArrayList<>();
        modAuthorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<? extends GrantedAuthority> authoritiesCollection = (Collection) modAuthorities;
        lenient().when(moderatorAuth.getAuthorities()).thenAnswer(invocation -> authoritiesCollection);

        when(userRepository.findById(3L)).thenReturn(Optional.of(moderatorUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            userService.updateUserStatus(3L, "BANNED", moderatorAuth)
        );
        verify(userRepository, never()).save(any(User.class));
    }
}
