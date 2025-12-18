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

import com.forumviajeros.backend.dto.post.PostRequestDTO;
import com.forumviajeros.backend.dto.post.PostResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.service.post.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Posts", description = "API para gestión de publicaciones en foros")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Obtener todos los posts", description = "Devuelve una lista paginada de todas las publicaciones")
    @ApiResponse(responseCode = "200", description = "Lista de posts obtenida con éxito")
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Pageable pageable) {
        log.debug("Obteniendo todos los posts con paginación: {}", pageable);
        return ResponseEntity.ok(postService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener post por ID", description = "Devuelve una publicación según su ID")
    @ApiResponse(responseCode = "200", description = "Post encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Post no encontrado", content = @Content)
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        log.debug("Obteniendo post con id: {}", id);
        try {
            return ResponseEntity.ok(postService.findById(id));
        } catch (Exception e) {
            log.warn("Post no encontrado con id: {}", id);
            throw new ResourceNotFoundException("Post", "id", id);
        }
    }

    @GetMapping("/forum/{forumId}")
    @Operation(summary = "Obtener posts por foro", description = "Devuelve todas las publicaciones de un foro específico")
    @ApiResponse(responseCode = "200", description = "Lista de posts obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "Foro no encontrado", content = @Content)
    public ResponseEntity<List<PostResponseDTO>> getPostsByForum(@PathVariable Long forumId) {
        log.debug("Obteniendo posts del foro con id: {}", forumId);
        try {
            return ResponseEntity.ok(postService.findByForum(forumId));
        } catch (Exception e) {
            log.warn("Error al obtener posts del foro con id: {}", forumId, e);
            throw new ResourceNotFoundException("Foro", "id", forumId);
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo post", description = "Crea una nueva publicación en un foro")
    @ApiResponse(responseCode = "201", description = "Post creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de post inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postDTO,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} creando nuevo post en foro {}", username, postDTO.getForumId());
        try {
            Long userId = postService.getUserIdByUsername(username);
            PostResponseDTO createdPost = postService.createPost(postDTO, userId);
            log.info("Post creado exitosamente con id: {} por usuario: {}", createdPost.getId(), username);
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear post para usuario: {}", username, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar post", description = "Actualiza una publicación existente")
    @ApiResponse(responseCode = "200", description = "Post actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Post no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar este post", content = @Content)
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id,
            @Valid @RequestBody PostRequestDTO postDTO,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} actualizando post con id: {}", username, id);
        try {
            PostResponseDTO updatedPost = postService.updatePost(id, postDTO, authentication);
            log.info("Post con id: {} actualizado exitosamente por usuario: {}", id, username);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            log.warn("Error al actualizar post con id: {} por usuario: {}", id, username, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar post", description = "Elimina una publicación por su ID")
    @ApiResponse(responseCode = "204", description = "Post eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Post no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este post", content = @Content)
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} eliminando post con id: {}", username, id);
        try {
            postService.delete(id, authentication);
            log.info("Post con id: {} eliminado exitosamente por usuario: {}", id, username);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("Error al eliminar post con id: {} por usuario: {}", id, username, e);
            throw e;
        }
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Subir imágenes a post", description = "Agrega una o más imágenes a una publicación")
    @ApiResponse(responseCode = "200", description = "Imágenes agregadas con éxito")
    @ApiResponse(responseCode = "404", description = "Post no encontrado", content = @Content)
    @ApiResponse(responseCode = "400", description = "Archivos inválidos o error al procesar", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar este post", content = @Content)
    public ResponseEntity<PostResponseDTO> uploadPostImages(@PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} subiendo {} imagen(es) al post con id: {}", username, files.size(), id);
        try {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException("Debe proporcionar al menos un archivo");
            }
            PostResponseDTO updatedPost = postService.addImages(id, files, authentication);
            log.info("Imágenes agregadas exitosamente al post con id: {} por usuario: {}", id, username);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al subir imágenes al post {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al subir imágenes al post {} por usuario {}: {}", id, username, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{postId}/images/{imageId}")
    @Operation(summary = "Eliminar imagen de post", description = "Elimina una imagen específica de una publicación")
    @ApiResponse(responseCode = "200", description = "Imagen eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "Post o imagen no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar este post", content = @Content)
    public ResponseEntity<PostResponseDTO> deletePostImage(@PathVariable Long postId,
            @PathVariable Long imageId,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("Usuario {} eliminando imagen {} del post {}", username, imageId, postId);
        try {
            PostResponseDTO updatedPost = postService.removeImage(postId, imageId, authentication);
            log.info("Imagen {} eliminada exitosamente del post {} por usuario: {}", imageId, postId, username);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            log.warn("Error al eliminar imagen {} del post {} por usuario {}: {}", imageId, postId, username, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Obtener posts del usuario actual", description = "Devuelve todas las publicaciones del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de posts obtenida con éxito")
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    public ResponseEntity<List<PostResponseDTO>> getCurrentUserPosts(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Obteniendo posts del usuario: {}", username);
        return ResponseEntity.ok(postService.findByCurrentUser(authentication));
    }
}