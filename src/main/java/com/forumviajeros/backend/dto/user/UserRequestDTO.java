package com.forumviajeros.backend.dto.user;

import com.forumviajeros.backend.validation.ValidEmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    private String username;

    @NotBlank(message = "El email no puede estar vacío")
    @ValidEmail
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre completo no puede exceder los 100 caracteres")
    private String fullName;

    @Size(max = 1000, message = "La biografía no puede exceder los 1000 caracteres")
    private String biography;

    @Size(max = 100, message = "La ubicación no puede exceder los 100 caracteres")
    private String location;

    @com.forumviajeros.backend.validation.ValidPassword
    private String password;

    public boolean isLoginRequest() {
        return username != null && password != null && email == null;
    }

    public boolean isRegistrationRequest() {
        return username != null && password != null && email != null;
    }

    public boolean isUpdateRequest() {
        return username != null && password == null && email != null;
    }
}
