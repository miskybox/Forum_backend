package com.forumviajeros.backend.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {

    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 60, message = "El nombre del rol no puede exceder los 60 caracteres")
    private String name;

    @Size(max = 200, message = "La descripción del rol no puede exceder los 200 caracteres")
    private String description;
}
