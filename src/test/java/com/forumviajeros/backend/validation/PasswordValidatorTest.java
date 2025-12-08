package com.forumviajeros.backend.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Tests para el validador de contraseñas
 */
class PasswordValidatorTest {

    private PasswordValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
        context = org.mockito.Mockito.mock(ConstraintValidatorContext.class);
        violationBuilder = org.mockito.Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        
        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(violationBuilder);
        org.mockito.Mockito.when(violationBuilder.addConstraintViolation())
                .thenReturn(context);
    }

    @Test
    @DisplayName("Debe aceptar contraseña válida con todos los requisitos")
    void shouldAcceptValidPassword() {
        String validPassword = "Password123!";
        assertTrue(validator.isValid(validPassword, context), 
                "Contraseña válida debe ser aceptada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña con menos de 8 caracteres")
    void shouldRejectShortPassword() {
        String shortPassword = "Pass1!";
        assertFalse(validator.isValid(shortPassword, context), 
                "Contraseña corta debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin mayúsculas")
    void shouldRejectPasswordWithoutUppercase() {
        String noUppercase = "password123!";
        assertFalse(validator.isValid(noUppercase, context), 
                "Contraseña sin mayúsculas debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin minúsculas")
    void shouldRejectPasswordWithoutLowercase() {
        String noLowercase = "PASSWORD123!";
        assertFalse(validator.isValid(noLowercase, context), 
                "Contraseña sin minúsculas debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin caracteres especiales")
    void shouldRejectPasswordWithoutSpecialChar() {
        String noSpecial = "Password123";
        assertFalse(validator.isValid(noSpecial, context), 
                "Contraseña sin caracteres especiales debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña null")
    void shouldRejectNullPassword() {
        assertFalse(validator.isValid(null, context), 
                "Contraseña null debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña vacía")
    void shouldRejectEmptyPassword() {
        assertFalse(validator.isValid("", context), 
                "Contraseña vacía debe ser rechazada");
    }

    @Test
    @DisplayName("Debe rechazar contraseña con solo espacios")
    void shouldRejectBlankPassword() {
        assertFalse(validator.isValid("   ", context), 
                "Contraseña con solo espacios debe ser rechazada");
    }

    @Test
    @DisplayName("Debe aceptar contraseña con diferentes caracteres especiales")
    void shouldAcceptPasswordWithDifferentSpecialChars() {
        String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "]", "{", "}", "|", ";", ":", ",", ".", "<", ">", "?"};
        
        for (String specialChar : specialChars) {
            String password = "Password1" + specialChar;
            assertTrue(validator.isValid(password, context), 
                    "Debe aceptar contraseña con carácter especial: " + specialChar);
        }
    }

    @Test
    @DisplayName("Debe aceptar contraseña con exactamente 8 caracteres")
    void shouldAcceptPasswordWithExactly8Chars() {
        String password = "Pass1!ab";
        assertTrue(validator.isValid(password, context), 
                "Debe aceptar contraseña con exactamente 8 caracteres");
    }

    @Test
    @DisplayName("Debe aceptar contraseña larga")
    void shouldAcceptLongPassword() {
        String longPassword = "VeryLongPassword123!WithManyCharacters";
        assertTrue(validator.isValid(longPassword, context), 
                "Debe aceptar contraseña larga");
    }

    @Test
    @DisplayName("Debe aceptar contraseña con múltiples mayúsculas y minúsculas")
    void shouldAcceptPasswordWithMultipleCases() {
        String password = "PaSsWoRd123!";
        assertTrue(validator.isValid(password, context), 
                "Debe aceptar contraseña con múltiples mayúsculas y minúsculas");
    }

    @Test
    @DisplayName("Debe aceptar contraseña con números")
    void shouldAcceptPasswordWithNumbers() {
        String password = "Password123!";
        assertTrue(validator.isValid(password, context), 
                "Debe aceptar contraseña con números");
    }
}

