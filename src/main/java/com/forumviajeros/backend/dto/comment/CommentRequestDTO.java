package com.forumviajeros.backend.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    private Long id;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 1000, message = "El contenido del comentario no puede exceder los 1000 caracteres")
    private String content;

    @NotNull(message = "La publicación del comentario es obligatoria")
    private Long postId;
}
