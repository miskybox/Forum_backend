package com.forumviajeros.backend.service.auth;

import com.forumviajeros.backend.dto.auth.AuthRequestDTO;
import com.forumviajeros.backend.dto.auth.AuthResponseDTO;
import com.forumviajeros.backend.dto.user.UserRegisterDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserResponseDTO register(UserRegisterDTO dto);

    AuthResponseDTO login(AuthRequestDTO dto);

    void logout(HttpServletRequest request, HttpServletResponse response);

    AuthResponseDTO refreshToken(String refreshToken);
}
