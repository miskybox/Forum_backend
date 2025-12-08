package com.forumviajeros.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador para verificar que una contraseña cumpla con los requisitos de seguridad:
 * - Mínimo 8 caracteres
 * - Al menos una mayúscula (A-Z)
 * - Al menos una minúscula (a-z)
 * - Al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No se necesita inicialización
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return false;
        }

        // Validar longitud mínima
        if (password.length() < MIN_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe tener al menos " + MIN_LENGTH + " caracteres")
                    .addConstraintViolation();
            return false;
        }

        // Validar mayúscula
        if (!password.matches(UPPERCASE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos una letra mayúscula")
                    .addConstraintViolation();
            return false;
        }

        // Validar minúscula
        if (!password.matches(LOWERCASE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos una letra minúscula")
                    .addConstraintViolation();
            return false;
        }

        // Validar carácter especial
        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

