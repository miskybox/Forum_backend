package com.forumviajeros.backend.controller;

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

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        return new ResponseEntity<>(authService.register(registerDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(authService.login(authRequestDTO));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario e invalida el token JWT")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada con éxito")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renueva el token JWT utilizando el refresh token")
    @ApiResponse(responseCode = "200", description = "Token renovado con éxito")
    @ApiResponse(responseCode = "401", description = "Token de refresco inválido", content = @Content)
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshRequest.getRefreshToken()));
    }
}