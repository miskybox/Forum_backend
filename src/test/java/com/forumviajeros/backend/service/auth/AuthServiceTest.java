package com.forumviajeros.backend.service.auth;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.forumviajeros.backend.dto.auth.AuthRequestDTO;
import com.forumviajeros.backend.dto.auth.AuthResponseDTO;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.exception.BadRequestException;
import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.RoleRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.token.RefreshTokenService;

/**
 * Tests unitarios para AuthService.
 * 
 * Este es un ejemplo de cómo deberían estructurarse los tests de servicios.
 * Usa Mockito para mockear dependencias y JUnit 5 para las aserciones.
 * 
 * NOTA: Este test puede necesitar ajustes según la estructura exacta del proyecto.
 * Úsalo como referencia para crear otros tests de servicios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role userRole;
    private UserRegisterDTO registerDTO;
    private AuthRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        // Configurar rol de prueba
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
        userRole.setDescription("Rol por defecto");

        // Configurar usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$hashedPassword"); // BCrypt hash simulado
        testUser.setRoles(new HashSet<>());
        testUser.getRoles().add(userRole);

        // Configurar DTOs de prueba
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("Password123!");

        loginDTO = new AuthRequestDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("Password123!");
    }

    @Test
    @DisplayName("Registro exitoso de nuevo usuario")
    void register_ShouldSucceed_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername(registerDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        UserResponseDTO result = authService.register(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(registerDTO.getUsername(), result.getUsername());
        assertEquals(registerDTO.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    @DisplayName("Registro falla cuando el username ya existe")
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(userRepository.findByUsername(registerDTO.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.register(registerDTO);
        });

        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registro falla cuando el email ya existe")
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.findByUsername(registerDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.register(registerDTO);
        });

        assertEquals("El correo electrónico ya está registrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login exitoso con username")
    void login_ShouldSucceed_WithValidUsername() {
        // Arrange
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(true);
        when(refreshTokenService.generateAccessToken(testUser.getUsername())).thenReturn("accessToken");
        when(refreshTokenService.generateRefreshToken(testUser.getUsername())).thenReturn("refreshToken");

        // Act
        AuthResponseDTO result = authService.login(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(refreshTokenService).generateAccessToken(testUser.getUsername());
        verify(refreshTokenService).generateRefreshToken(testUser.getUsername());
    }

    @Test
    @DisplayName("Login falla con credenciales inválidas")
    void login_ShouldThrowException_WithInvalidCredentials() {
        // Arrange
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(refreshTokenService, never()).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("Login falla cuando el usuario no existe")
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(refreshTokenService, never()).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("Login con email en lugar de username")
    void login_ShouldSucceed_WithEmail() {
        // Arrange
        loginDTO.setUsername("test@example.com");
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginDTO.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(true);
        when(refreshTokenService.generateAccessToken(testUser.getUsername())).thenReturn("accessToken");
        when(refreshTokenService.generateRefreshToken(testUser.getUsername())).thenReturn("refreshToken");

        // Act
        AuthResponseDTO result = authService.login(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        verify(userRepository).findByEmail(loginDTO.getUsername());
    }
}

