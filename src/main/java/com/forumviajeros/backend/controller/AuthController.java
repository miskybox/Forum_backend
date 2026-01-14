package com.forumviajeros.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.auth.AuthRequestDTO;
import com.forumviajeros.backend.dto.auth.AuthResponseDTO;
import com.forumviajeros.backend.dto.auth.RefreshTokenRequestDTO;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API para autenticación de usuarios")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            logger.info("Intento de registro para usuario: {}", registerDTO.getUsername());

            logger.debug("Datos de registro - Username: {}, Email: {}",
                    registerDTO.getUsername(), registerDTO.getEmail());

            UserResponseDTO response = authService.register(registerDTO);
            logger.info("Usuario registrado exitosamente: {}", registerDTO.getUsername());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Para errores de validación o usuario existente
            logger.warn("Error de validación en registro: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            // Generic message to avoid user enumeration
            errorResponse.put("message", "Los datos proporcionados no son válidos. Por favor, verifica e intenta nuevamente.");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Log detallado para cualquier otra excepción
            logger.error("Error interno al registrar usuario: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            // Generic error message without exposing internal details
            errorResponse.put("message", "Error al procesar el registro. Por favor, intenta nuevamente más tarde.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        try {
            logger.info("Intento de inicio de sesión para: {}", authRequestDTO.getUsername());
            AuthResponseDTO response = authService.login(authRequestDTO);
            logger.info("Inicio de sesión exitoso para: {}", authRequestDTO.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error en inicio de sesión para usuario: {}", authRequestDTO.getUsername());
            // Don't log the actual error message to avoid information leakage
            Map<String, String> errorResponse = new HashMap<>();
            // Generic message to prevent username enumeration
            errorResponse.put("message", "Credenciales inválidas. Por favor, verifica tu usuario y contraseña.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario e invalida el token JWT")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada con éxito")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.logout(request, response);
            logger.info("Sesión cerrada exitosamente");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al cerrar sesión: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renueva el token JWT utilizando el refresh token")
    @ApiResponse(responseCode = "200", description = "Token renovado con éxito")
    @ApiResponse(responseCode = "401", description = "Token de refresco inválido", content = @Content)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshRequest) {
        try {
            logger.info("Intento de renovación de token");
            AuthResponseDTO response = authService.refreshToken(refreshRequest.getRefreshToken());
            logger.info("Token renovado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al renovar token: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al renovar token: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }
}