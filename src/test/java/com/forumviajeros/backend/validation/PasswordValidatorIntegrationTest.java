package com.forumviajeros.backend.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

/**
 * Tests de integración para la validación de contraseñas
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordValidatorIntegrationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe validar contraseña válida en UserRegisterDTO")
    void shouldValidateValidPassword() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "No debe haber violaciones para contraseña válida");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin mayúscula")
    void shouldRejectPasswordWithoutUppercase() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("invalidpass123!");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debe haber violaciones");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("mayúscula")),
                "Debe indicar falta de mayúscula");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin minúscula")
    void shouldRejectPasswordWithoutLowercase() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("INVALIDPASS123!");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debe haber violaciones");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("minúscula")),
                "Debe indicar falta de minúscula");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin carácter especial")
    void shouldRejectPasswordWithoutSpecialChar() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("InvalidPass123");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debe haber violaciones");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("carácter especial")),
                "Debe indicar falta de carácter especial");
    }

    @Test
    @DisplayName("Debe rechazar contraseña con menos de 8 caracteres")
    void shouldRejectShortPassword() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("Pass1!");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debe haber violaciones");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("8 caracteres")),
                "Debe indicar longitud insuficiente");
    }

    @Test
    @DisplayName("Debe rechazar contraseña null")
    void shouldRejectNullPassword() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword(null);

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Debe haber violaciones");
    }
}

