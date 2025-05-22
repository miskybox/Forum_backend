package com.forumviajeros.backend.service.auth;

import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.auth.AuthRequestDTO;
import com.forumviajeros.backend.dto.auth.AuthResponseDTO;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.exception.BadRequestException;
import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.RoleRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.token.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        logger.info("Intentando iniciar sesión para usuario: {}", authRequestDTO.getUsername());
        Optional<User> userOpt = userRepository.findByUsername(authRequestDTO.getUsername());
        if (userOpt.isEmpty()) {
            logger.warn("Usuario no encontrado: {}", authRequestDTO.getUsername());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(authRequestDTO.getPassword(), user.getPassword())) {
            logger.warn("Contraseña incorrecta para usuario: {}", authRequestDTO.getUsername());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        String accessToken = refreshTokenService.generateAccessToken(user.getUsername());
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUsername());
        logger.info("Inicio de sesión exitoso para usuario: {}", authRequestDTO.getUsername());
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO dto) {
        logger.info("Iniciando registro para usuario: {}", dto.getUsername());

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            logger.warn("Intento de registro con nombre de usuario existente: {}", dto.getUsername());
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Intento de registro con email existente: {}", dto.getEmail());
            throw new BadRequestException("El correo electrónico ya está registrado");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(new HashSet<>());

        try {
            Optional<Role> userRoleOpt = roleRepository.findByName("ROLE_USER");

            if (userRoleOpt.isPresent()) {
                user.getRoles().add(userRoleOpt.get());
                logger.info("Rol ROLE_USER asignado al usuario: {}", dto.getUsername());
            } else {
                logger.warn("Rol ROLE_USER no encontrado. Creando rol...");
                Role newUserRole = new Role();
                newUserRole.setName("ROLE_USER");
                Role savedRole = roleRepository.save(newUserRole);

                user.getRoles().add(savedRole);
                logger.info("Nuevo rol ROLE_USER creado y asignado al usuario: {}", dto.getUsername());
            }
        } catch (Exception e) {
            logger.error("Error al asignar rol al usuario: {}", e.getMessage(), e);
            throw new BadRequestException("Error interno al asignar el rol al usuario");
        }

        logger.info("Guardando nuevo usuario: {}", dto.getUsername());
        User savedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());

        logger.info("Usuario registrado exitosamente: {}", dto.getUsername());
        return response;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = refreshTokenService.extractRefreshTokenFromRequest(request);

        if (refreshToken != null) {
            refreshTokenService.removeToken(refreshToken);
            logger.info("Sesión cerrada y token eliminado");
        }
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        String username = refreshTokenService.getUsernameFromToken(refreshToken);

        if (username == null) {
            logger.warn("Intento de refresh con token inválido");
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            refreshTokenService.removeToken(refreshToken);
            logger.warn("Usuario no encontrado durante refresh de token: '{}'", username);
            throw new BadCredentialsException("Usuario no encontrado");
        }

        String newAccessToken = refreshTokenService.generateAccessToken(username);
        refreshTokenService.removeToken(refreshToken);
        String newRefreshToken = refreshTokenService.generateRefreshToken(username);

        logger.debug("Tokens renovados para: '{}'", username);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}