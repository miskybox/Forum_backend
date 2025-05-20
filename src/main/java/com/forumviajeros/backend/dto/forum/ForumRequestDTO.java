package com.forumviajeros.backend.dto.forum;

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
public class ForumRequestDTO {
    private Long id;

    @NotBlank(message = "El título del foro es obligatorio")
    @Size(max = 100, message = "El título del foro no puede exceder los 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción del foro es obligatoria")
    @Size(max = 1000, message = "La descripción del foro no puede exceder los 1000 caracteres")
    private String description;

    @NotNull(message = "La categoría del foro es obligatoria")
    private Long categoryId;

    private List<String> tags;

    private String status;
}
