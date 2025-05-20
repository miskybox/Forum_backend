package com.forumviajeros.backend.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.comment.CommentRequestDTO;
import com.forumviajeros.backend.dto.comment.CommentResponseDTO;
import com.forumviajeros.backend.service.comment.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "API para gestión de comentarios en publicaciones")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Obtener todos los comentarios", description = "Devuelve un listado de todos los comentarios")
    @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida con éxito")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener comentario por ID", description = "Devuelve un comentario según su ID")
    @ApiResponse(responseCode = "200", description = "Comentario encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Obtener comentarios por publicación", description = "Devuelve todos los comentarios de una publicación específica")
    @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida con éxito")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PostMapping("/post/{postId}")
    @Operation(summary = "Crear nuevo comentario", description = "Crea un nuevo comentario en una publicación específica")
    @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de comentario inválidos", content = @Content)
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDTO commentDTO,
            Authentication authentication) {
        return new ResponseEntity<>(commentService.createComment(commentDTO, authentication, postId),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar comentario", description = "Actualiza un comentario existente")
    @ApiResponse(responseCode = "200", description = "Comentario actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para editar este comentario", content = @Content)
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO commentDTO,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDTO, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar comentario", description = "Elimina un comentario por su ID")
    @ApiResponse(responseCode = "204", description = "Comentario eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este comentario", content = @Content)
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
