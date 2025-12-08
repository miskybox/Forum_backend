package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.forumviajeros.backend.model.Role;

/**
 * Tests de integración para RoleRepository con PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Debe encontrar ROLE_USER")
    void shouldFindRoleUser() {
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        assertTrue(role.isPresent(), "ROLE_USER debe existir");
        assertEquals("ROLE_USER", role.get().getName());
    }

    @Test
    @DisplayName("Debe encontrar ROLE_MODERATOR")
    void shouldFindRoleModerator() {
        Optional<Role> role = roleRepository.findByName("ROLE_MODERATOR");
        assertTrue(role.isPresent(), "ROLE_MODERATOR debe existir");
        assertEquals("ROLE_MODERATOR", role.get().getName());
    }

    @Test
    @DisplayName("Debe encontrar ROLE_ADMIN")
    void shouldFindRoleAdmin() {
        Optional<Role> role = roleRepository.findByName("ROLE_ADMIN");
        assertTrue(role.isPresent(), "ROLE_ADMIN debe existir");
        assertEquals("ROLE_ADMIN", role.get().getName());
    }

    @Test
    @DisplayName("Debe guardar un nuevo rol")
    void shouldSaveRole() {
        Role role = new Role();
        role.setName("ROLE_TEST");
        role.setDescription("Rol de prueba");

        Role saved = roleRepository.save(role);

        assertNotNull(saved.getId(), "Rol debe tener ID generado");
        assertEquals("ROLE_TEST", saved.getName());
        assertEquals("Rol de prueba", saved.getDescription());
    }

    @Test
    @DisplayName("Debe mantener unicidad de nombre de rol")
    void shouldEnforceRoleNameUniqueness() {
        Role role1 = new Role();
        role1.setName("ROLE_UNIQUE");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("ROLE_UNIQUE");

        assertThrows(Exception.class, () -> {
            roleRepository.save(role2);
            roleRepository.flush();
        }, "Debe lanzar excepción por nombre duplicado");
    }

    @Test
    @DisplayName("Debe actualizar rol")
    void shouldUpdateRole() {
        Role role = new Role();
        role.setName("ROLE_UPDATE");
        role.setDescription("Descripción original");
        Role saved = roleRepository.save(role);

        saved.setDescription("Descripción actualizada");
        Role updated = roleRepository.save(saved);

        assertEquals("Descripción actualizada", updated.getDescription());
    }

    @Test
    @DisplayName("Debe eliminar rol")
    void shouldDeleteRole() {
        Role role = new Role();
        role.setName("ROLE_DELETE");
        Role saved = roleRepository.save(role);
        Long id = saved.getId();

        roleRepository.delete(saved);

        assertFalse(roleRepository.findById(id).isPresent(), "Rol debe estar eliminado");
    }

    @Test
    @DisplayName("Debe verificar que existen los tres roles principales")
    void shouldHaveMainRoles() {
        assertTrue(roleRepository.findByName("ROLE_USER").isPresent(), 
                "ROLE_USER debe existir");
        assertTrue(roleRepository.findByName("ROLE_MODERATOR").isPresent(), 
                "ROLE_MODERATOR debe existir");
        assertTrue(roleRepository.findByName("ROLE_ADMIN").isPresent(), 
                "ROLE_ADMIN debe existir");
    }
}

