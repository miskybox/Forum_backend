package com.forumviajeros.backend.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.role.RoleRequestDTO;
import com.forumviajeros.backend.dto.role.RoleResponseDTO;
import com.forumviajeros.backend.service.role.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para gestión de roles de usuario")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Obtener todos los roles", description = "Devuelve todos los roles disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida con éxito")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID", description = "Devuelve un rol según su ID")
    @ApiResponse(responseCode = "200", description = "Rol encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRole(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de rol inválidos", content = @Content)
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO roleDTO) {
        return new ResponseEntity<>(roleService.createRole(roleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar rol", description = "Actualiza un rol existente")
    @ApiResponse(responseCode = "200", description = "Rol actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar rol", description = "Elimina un rol por su ID")
    @ApiResponse(responseCode = "204", description = "Rol eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}