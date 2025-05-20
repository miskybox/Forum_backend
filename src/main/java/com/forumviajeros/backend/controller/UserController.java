package com.forumviajeros.backend.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.user.UserRequestDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
        UserResponseDTO user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos", content = @Content)
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO userDTO,
            @RequestParam(required = false) List<String> roles) {

        // Si roles vienen vacíos, asigna un rol por defecto (por ejemplo "USER")
        if (roles == null || roles.isEmpty()) {
            roles = List.of("USER");
        }

        UserResponseDTO createdUser = userService.registerUser(userDTO, Set.copyOf(roles));
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Método para cambiar la contraseña - ejemplo de endpoint adicional
    @PutMapping("/{id}/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Permite cambiar la contraseña de un usuario")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    public ResponseEntity<UserResponseDTO> changePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        UserResponseDTO user = userService.changePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok(user);
    }
}
