package com.forumviajeros.backend.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.user.ChangePasswordRequestDTO;
import com.forumviajeros.backend.dto.user.UserRequestDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve un listado de todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario según su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.debug("Obteniendo usuario con id: {}", id);
        try {
            UserResponseDTO user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.warn("Usuario no encontrado con id: {}", id);
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve los datos del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Usuario actual obtenido")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Obteniendo datos del usuario actual: {}", username);
        try {
            UserResponseDTO user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.warn("Error al obtener usuario actual {}: {}", username, e.getMessage());
            throw new ResourceNotFoundException("Usuario", "username", username);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado (solo ADMIN)", content = @Content)
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO userDTO,
            @RequestParam(required = false) List<String> roles) {
        log.info("Admin creando nuevo usuario: {}", userDTO.getUsername());
        try {
            if (roles == null || roles.isEmpty()) {
                roles = List.of("USER");
            }

            UserResponseDTO createdUser = userService.registerUser(userDTO, Set.copyOf(roles));
            log.info("Usuario creado exitosamente con id: {} y roles: {}", createdUser.getId(), roles);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear usuario {}: {}", userDTO.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar este usuario", content = @Content)
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userDTO,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} actualizando usuario con id: {}", username, id);
        try {
            assertCanManageUser(id, authentication);
            UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
            log.info("Usuario con id: {} actualizado exitosamente por: {}", id, username);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.warn("Error al actualizar usuario {} por {}: {}", id, username, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado (solo ADMIN)", content = @Content)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin eliminando usuario con id: {}", id);
        try {
            userService.deleteUser(id);
            log.info("Usuario con id: {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("Error al eliminar usuario {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Permite cambiar la contraseña de un usuario")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada con éxito")
    @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o nueva contraseña inválida", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para cambiar la contraseña de este usuario", content = @Content)
    public ResponseEntity<UserResponseDTO> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} cambiando contraseña del usuario con id: {}", username, id);
        try {
            assertCanManageUser(id, authentication);
            UserResponseDTO user = userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            log.info("Contraseña cambiada exitosamente para usuario {} por: {}", id, username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.warn("Error al cambiar contraseña del usuario {} por {}: {}", id, username, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar roles de usuario", description = "Actualiza los roles asignados a un usuario")
    @ApiResponse(responseCode = "200", description = "Roles actualizados con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody List<String> roles) {
        UserResponseDTO user = userService.updateUserRoles(id, Set.copyOf(roles));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Actualizar estado del usuario", description = "Permite a moderadores/admins banear o cambiar el estado de un usuario")
    @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado o intento de banear admin", content = @Content)
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} cambiando estado del usuario {} a: {}", username, id, status);
        try {
            UserResponseDTO user = userService.updateUserStatus(id, status, authentication);
            log.info("Estado del usuario {} actualizado a {} por: {}", id, status, username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.warn("Error al cambiar estado del usuario {} por {}: {}", id, username, e.getMessage());
            throw e;
        }
    }

    private void assertCanManageUser(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Usuario no autenticado");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        String username = authentication.getName();
        UserResponseDTO currentUser = userService.getUserByUsername(username);
        if (!currentUser.getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permisos para modificar este usuario");
        }
    }
}
