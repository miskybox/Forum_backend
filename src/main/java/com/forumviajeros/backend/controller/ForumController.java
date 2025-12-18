package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.forum.ForumRequestDTO;
import com.forumviajeros.backend.dto.forum.ForumResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.service.forum.ForumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/forums")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Forums", description = "API para gestión de foros")
public class ForumController {

    private final ForumService forumService;

    @GetMapping
    @Operation(summary = "Obtener todos los foros", description = "Devuelve un listado paginado de todos los foros")
    @ApiResponse(responseCode = "200", description = "Lista de foros obtenida con éxito")
    public ResponseEntity<Page<ForumResponseDTO>> getAllForums(Pageable pageable) {
        return ResponseEntity.ok(forumService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener foro por ID", description = "Devuelve un foro según su ID")
    @ApiResponse(responseCode = "200", description = "Foro encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    public ResponseEntity<ForumResponseDTO> getForumById(@PathVariable Long id) {
        log.debug("Obteniendo foro con id: {}", id);
        try {
            return ResponseEntity.ok(forumService.findById(id));
        } catch (Exception e) {
            log.warn("Foro no encontrado con id: {}", id);
            throw new ResourceNotFoundException("Foro", "id", id);
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtener foros por categoría", description = "Devuelve todos los foros de una categoría específica")
    @ApiResponse(responseCode = "200", description = "Lista de foros obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    public ResponseEntity<List<ForumResponseDTO>> getForumsByCategory(@PathVariable Long categoryId) {
        log.debug("Obteniendo foros de la categoría con id: {}", categoryId);
        try {
            return ResponseEntity.ok(forumService.findByCategory(categoryId));
        } catch (Exception e) {
            log.warn("Error al obtener foros de la categoría {}: {}", categoryId, e.getMessage());
            throw new ResourceNotFoundException("Categoría", "id", categoryId);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar foros", description = "Busca foros que coincidan con las palabras clave")
    @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda obtenidos con éxito")
    @ApiResponse(responseCode = "400", description = "Palabra clave inválida", content = @Content)
    public ResponseEntity<List<ForumResponseDTO>> searchForums(@RequestParam String keyword) {
        log.debug("Buscando foros con palabra clave: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("Intento de búsqueda con palabra clave vacía");
            throw new IllegalArgumentException("La palabra clave no puede estar vacía");
        }
        return ResponseEntity.ok(forumService.searchByKeyword(keyword.trim()));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo foro", description = "Crea un nuevo foro en una categoría específica")
    @ApiResponse(responseCode = "201", description = "Foro creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de foro inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    public ResponseEntity<ForumResponseDTO> createForum(@Valid @RequestBody ForumRequestDTO forumDTO,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} creando nuevo foro en categoría {}", username, forumDTO.getCategoryId());
        try {
            Long userId = forumService.getUserIdByUsername(username);
            ForumResponseDTO createdForum = forumService.createForum(forumDTO, userId);
            log.info("Foro creado exitosamente con id: {} por usuario: {}", createdForum.getId(), username);
            return new ResponseEntity<>(createdForum, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear foro para usuario {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar foro", description = "Actualiza un foro existente")
    @ApiResponse(responseCode = "200", description = "Foro actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para editar este foro", content = @Content)
    public ResponseEntity<ForumResponseDTO> updateForum(@PathVariable Long id,
            @Valid @RequestBody ForumRequestDTO forumDTO,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} actualizando foro con id: {}", username, id);
        try {
            ForumResponseDTO updatedForum = forumService.updateForum(id, forumDTO, authentication);
            log.info("Foro con id: {} actualizado exitosamente por usuario: {}", id, username);
            return ResponseEntity.ok(updatedForum);
        } catch (Exception e) {
            log.warn("Error al actualizar foro {} por usuario {}: {}", id, username, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar foro", description = "Elimina un foro por su ID")
    @ApiResponse(responseCode = "204", description = "Foro eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este foro", content = @Content)
    public ResponseEntity<Void> deleteForum(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} eliminando foro con id: {}", username, id);
        try {
            forumService.delete(id, authentication);
            log.info("Foro con id: {} eliminado exitosamente por usuario: {}", id, username);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("Error al eliminar foro {} por usuario {}: {}", id, username, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Subir imagen de foro", description = "Sube una imagen para el foro")
    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "400", description = "Archivo inválido o error al procesar", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para editar este foro", content = @Content)
    public ResponseEntity<ForumResponseDTO> uploadForumImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} subiendo imagen al foro con id: {}", username, id);
        try {
            if (file == null || file.isEmpty()) {
                log.warn("Intento de subir archivo vacío al foro {} por usuario: {}", id, username);
                throw new IllegalArgumentException("El archivo no puede estar vacío");
            }
            ForumResponseDTO updatedForum = forumService.updateImage(id, file, authentication);
            log.info("Imagen subida exitosamente al foro {} por usuario: {}", id, username);
            return ResponseEntity.ok(updatedForum);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al subir imagen al foro {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al subir imagen al foro {} por usuario {}: {}", id, username, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Obtener foros del usuario actual", description = "Devuelve todos los foros creados por el usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de foros obtenida con éxito")
    public ResponseEntity<List<ForumResponseDTO>> getCurrentUserForums(Authentication authentication) {
        return ResponseEntity.ok(forumService.findByCurrentUser(authentication));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado del foro", description = "Permite a moderadores/admins cerrar, archivar o reactivar un foro")
    @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar este foro", content = @Content)
    public ResponseEntity<ForumResponseDTO> updateForumStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} cambiando estado del foro {} a: {}", username, id, status);
        try {
            ForumResponseDTO forum = forumService.updateForumStatus(id, status, authentication);
            log.info("Estado del foro {} actualizado a {} por: {}", id, status, username);
            return ResponseEntity.ok(forum);
        } catch (Exception e) {
            log.warn("Error al cambiar estado del foro {} por {}: {}", id, username, e.getMessage());
            throw e;
        }
    }
}