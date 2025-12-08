package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;

/**
 * Tests de integración para UserRepository con PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Crear rol de usuario
        userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole = roleRepository.save(userRole);

        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFullName("Test User");
        testUser.setStatus(User.UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Debe guardar un usuario en la base de datos")
    void shouldSaveUser() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser.getId(), "Usuario debe tener ID generado");
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("Debe encontrar usuario por username")
    void shouldFindUserByUsername() {
        userRepository.save(testUser);
        
        Optional<User> found = userRepository.findByUsername("testuser");
        
        assertTrue(found.isPresent(), "Usuario debe existir");
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void shouldFindUserByEmail() {
        userRepository.save(testUser);
        
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        assertTrue(found.isPresent(), "Usuario debe existir");
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Debe encontrar usuario por username o email")
    void shouldFindUserByUsernameOrEmail() {
        userRepository.save(testUser);
        
        Optional<User> byUsername = userRepository.findByUsernameOrEmail("testuser", "other@example.com");
        Optional<User> byEmail = userRepository.findByUsernameOrEmail("otheruser", "test@example.com");
        
        assertTrue(byUsername.isPresent(), "Debe encontrar por username");
        assertTrue(byEmail.isPresent(), "Debe encontrar por email");
    }

    @Test
    @DisplayName("Debe verificar si username existe")
    void shouldCheckUsernameExists() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByUsername("testuser"), "Username debe existir");
        assertFalse(userRepository.existsByUsername("nonexistent"), "Username no debe existir");
    }

    @Test
    @DisplayName("Debe verificar si email existe")
    void shouldCheckEmailExists() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByEmail("test@example.com"), "Email debe existir");
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"), "Email no debe existir");
    }

    @Test
    @DisplayName("Debe actualizar usuario")
    void shouldUpdateUser() {
        User saved = userRepository.save(testUser);
        saved.setFullName("Updated Name");
        saved.setBiography("Updated biography");
        
        User updated = userRepository.save(saved);
        
        assertEquals("Updated Name", updated.getFullName());
        assertEquals("Updated biography", updated.getBiography());
    }

    @Test
    @DisplayName("Debe eliminar usuario")
    void shouldDeleteUser() {
        User saved = userRepository.save(testUser);
        Long id = saved.getId();
        
        userRepository.delete(saved);
        
        Optional<User> deleted = userRepository.findById(id);
        assertFalse(deleted.isPresent(), "Usuario debe estar eliminado");
    }

    @Test
    @DisplayName("Debe mantener unicidad de username")
    void shouldEnforceUsernameUniqueness() {
        userRepository.save(testUser);
        
        User duplicateUser = new User();
        duplicateUser.setUsername("testuser"); // Mismo username
        duplicateUser.setEmail("different@example.com");
        duplicateUser.setPassword("password123");
        
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }, "Debe lanzar excepción por username duplicado");
    }

    @Test
    @DisplayName("Debe mantener unicidad de email")
    void shouldEnforceEmailUniqueness() {
        userRepository.save(testUser);
        
        User duplicateUser = new User();
        duplicateUser.setUsername("differentuser");
        duplicateUser.setEmail("test@example.com"); // Mismo email
        duplicateUser.setPassword("password123");
        
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }, "Debe lanzar excepción por email duplicado");
    }
}

