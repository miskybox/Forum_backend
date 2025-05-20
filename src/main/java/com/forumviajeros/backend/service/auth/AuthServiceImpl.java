package com.forumviajeros.backend.service.auth;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.forumviajeros.backend.dto.auth.AuthRequestDTO;
import com.forumviajeros.backend.dto.auth.AuthResponseDTO;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.security.constants.SecurityConstants;
import com.forumviajeros.backend.service.token.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public UserResponseDTO register(UserRegisterDTO dto) {
        // Validar si usuario ya existe
        Optional<User> existingUser = userRepository.findByUsername(dto.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Crear nuevo usuario y cifrar contraseña
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        // Mapear a DTO de respuesta (sin password)
        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());

        return response;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        // Buscar usuario por username (podría ser email o username)
        Optional<User> optionalUser = userRepository.findByUsername(dto.getUsername());

        // Si no se encuentra, intentar buscar por email si parece un email
        if (optionalUser.isEmpty() && dto.getUsername().contains("@")) {
            optionalUser = userRepository.findByEmail(dto.getUsername());
        }

        if (optionalUser.isEmpty() ||
                !passwordEncoder.matches(dto.getPassword(), optionalUser.get().getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        User user = optionalUser.get();

        // Generar access token
        String accessToken = refreshTokenService.generateAccessToken(user.getUsername());

        // Generar refresh token
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(SecurityConstants.TOKEN_PREFIX + accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Obtener el refresh token del request
        String refreshToken = refreshTokenService.extractRefreshTokenFromRequest(request);

        if (refreshToken != null) {
            // Invalidar el refresh token
            refreshTokenService.removeToken(refreshToken);
        }
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        // Verificar si el refresh token existe en nuestro almacén
        String username = refreshTokenService.getUsernameFromToken(refreshToken);

        if (username == null) {
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }

        // Opcional: verificar si el usuario sigue existiendo y está activo
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            // Si el usuario ya no existe, invalidar su refresh token
            refreshTokenService.removeToken(refreshToken);
            throw new BadCredentialsException("Usuario no encontrado");
        }

        // Generar nuevo access token
        String newAccessToken = refreshTokenService.generateAccessToken(username);

        // Opcionalmente, generar un nuevo refresh token y invalidar el anterior
        refreshTokenService.removeToken(refreshToken);
        String newRefreshToken = refreshTokenService.generateRefreshToken(username);

        return AuthResponseDTO.builder()
                .accessToken(SecurityConstants.TOKEN_PREFIX + newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}