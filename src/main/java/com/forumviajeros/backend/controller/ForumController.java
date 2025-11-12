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
import com.forumviajeros.backend.service.forum.ForumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/forums")
@RequiredArgsConstructor
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
        return ResponseEntity.ok(forumService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtener foros por categoría", description = "Devuelve todos los foros de una categoría específica")
    @ApiResponse(responseCode = "200", description = "Lista de foros obtenida con éxito")
    public ResponseEntity<List<ForumResponseDTO>> getForumsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(forumService.findByCategory(categoryId));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar foros", description = "Busca foros que coincidan con las palabras clave")
    @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda obtenidos con éxito")
    public ResponseEntity<List<ForumResponseDTO>> searchForums(@RequestParam String keyword) {
        return ResponseEntity.ok(forumService.searchByKeyword(keyword));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo foro", description = "Crea un nuevo foro en una categoría específica")
    @ApiResponse(responseCode = "201", description = "Foro creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de foro inválidos", content = @Content)
    public ResponseEntity<ForumResponseDTO> createForum(@Valid @RequestBody ForumRequestDTO forumDTO,
            Authentication authentication) {
        String username = authentication.getName();
        Long userId = forumService.getUserIdByUsername(username);
        return new ResponseEntity<>(forumService.createForum(forumDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar foro", description = "Actualiza un foro existente")
    @ApiResponse(responseCode = "200", description = "Foro actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para editar este foro", content = @Content)
    public ResponseEntity<ForumResponseDTO> updateForum(@PathVariable Long id,
            @Valid @RequestBody ForumRequestDTO forumDTO,
            Authentication authentication) {
        return ResponseEntity.ok(forumService.updateForum(id, forumDTO, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar foro", description = "Elimina un foro por su ID")
    @ApiResponse(responseCode = "204", description = "Foro eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este foro", content = @Content)
    public ResponseEntity<Void> deleteForum(@PathVariable Long id, Authentication authentication) {
        forumService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Subir imagen de foro", description = "Sube una imagen para el foro")
    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para editar este foro", content = @Content)
    public ResponseEntity<ForumResponseDTO> uploadForumImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        return ResponseEntity.ok(forumService.updateImage(id, file, authentication));
    }

    @GetMapping("/user")
    @Operation(summary = "Obtener foros del usuario actual", description = "Devuelve todos los foros creados por el usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de foros obtenida con éxito")
    public ResponseEntity<List<ForumResponseDTO>> getCurrentUserForums(Authentication authentication) {
        return ResponseEntity.ok(forumService.findByCurrentUser(authentication));
    }
}