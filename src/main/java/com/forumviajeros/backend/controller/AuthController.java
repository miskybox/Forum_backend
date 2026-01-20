package com.forumviajeros.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
import com.forumviajeros.backend.util.CookieUtil;

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
    private static final String MESSAGE_KEY = "message";

    private final AuthService authService;
    private final CookieUtil cookieUtil;

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
            errorResponse.put(MESSAGE_KEY, "Los datos proporcionados no son válidos. Por favor, verifica e intenta nuevamente.");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Log detallado para cualquier otra excepción
            logger.error("Error interno al registrar usuario: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            // Generic error message without exposing internal details
            errorResponse.put(MESSAGE_KEY, "Error al procesar el registro. Por favor, intenta nuevamente más tarde.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve tokens JWT en cookies HttpOnly")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        try {
            logger.info("Intento de inicio de sesión para: {}", authRequestDTO.getUsername());
            AuthResponseDTO response = authService.login(authRequestDTO);
            logger.info("Inicio de sesión exitoso para: {}", authRequestDTO.getUsername());

            // Create HttpOnly cookies for tokens
            ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(response.getAccessToken());
            ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());

            // Return success response with cookies (tokens not in body for security)
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put(MESSAGE_KEY, "Inicio de sesión exitoso");
            successResponse.put("authenticated", true);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(successResponse);
        } catch (Exception e) {
            logger.error("Error en inicio de sesión para usuario: {}", authRequestDTO.getUsername());
            // Don't log the actual error message to avoid information leakage
            Map<String, String> errorResponse = new HashMap<>();
            // Generic message to prevent username enumeration
            errorResponse.put(MESSAGE_KEY, "Credenciales inválidas. Por favor, verifica tu usuario y contraseña.");
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

            // Clear HttpOnly cookies
            ResponseCookie expiredAccessCookie = cookieUtil.createExpiredCookie(CookieUtil.ACCESS_TOKEN_COOKIE, "/");
            ResponseCookie expiredRefreshCookie = cookieUtil.createExpiredCookie(CookieUtil.REFRESH_TOKEN_COOKIE, "/api/auth");

            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString())
                    .build();
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(MESSAGE_KEY, "Error al cerrar sesión. Por favor, intenta nuevamente.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renueva el token JWT utilizando el refresh token desde cookie")
    @ApiResponse(responseCode = "200", description = "Token renovado con éxito")
    @ApiResponse(responseCode = "401", description = "Token de refresco inválido", content = @Content)
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
            @RequestBody(required = false) RefreshTokenRequestDTO refreshRequest) {
        try {
            logger.info("Intento de renovación de token");

            // Try to get refresh token from cookie first, then from request body (backward compatibility)
            String refreshToken = cookieUtil.getRefreshTokenFromCookies(request);
            if (refreshToken == null && refreshRequest != null) {
                refreshToken = refreshRequest.getRefreshToken();
            }

            if (refreshToken == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put(MESSAGE_KEY, "No se encontró token de refresco.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            AuthResponseDTO response = authService.refreshToken(refreshToken);
            logger.info("Token renovado exitosamente");

            // Create new HttpOnly cookies for tokens
            ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(response.getAccessToken());
            ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put(MESSAGE_KEY, "Token renovado exitosamente");
            successResponse.put("authenticated", true);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(successResponse);
        } catch (Exception e) {
            logger.error("Error al renovar token: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(MESSAGE_KEY, "Token de refresco inválido o expirado.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }
}