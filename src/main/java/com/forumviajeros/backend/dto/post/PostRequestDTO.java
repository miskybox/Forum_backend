package com.forumviajeros.backend.dto.post;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    @NotBlank(message = "El título de la publicación es obligatorio")
    @Size(max = 100, message = "El título de la publicación no puede exceder los 100 caracteres")
    private String title;

    @NotBlank(message = "El contenido de la publicación es obligatorio")
    @Size(max = 5000, message = "El contenido de la publicación no puede exceder los 5000 caracteres")
    private String content;

    @NotNull(message = "El foro de la publicación es obligatorio")
    private Long forumId;

    private List<String> tags;

    private String status;
}
