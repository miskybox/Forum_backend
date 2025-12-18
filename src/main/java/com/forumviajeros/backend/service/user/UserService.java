package com.forumviajeros.backend.service.user;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;

import com.forumviajeros.backend.dto.user.UserRequestDTO;
import com.forumviajeros.backend.dto.user.UserResponseDTO;
import com.forumviajeros.backend.model.User;

public interface UserService {

    UserResponseDTO getUserByUsername(String username);

    User findByUsername(String username);

    UserResponseDTO registerUser(UserRequestDTO userDTO, Set<String> roles);

    UserResponseDTO updateUser(Long id, UserRequestDTO userDTO);

    UserResponseDTO getUser(Long id);

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long id);

    UserResponseDTO changePassword(Long id, String currentPassword, String newPassword);

    UserResponseDTO updateUserRoles(Long id, Set<String> roles);

    UserResponseDTO updateUserStatus(Long id, String status, Authentication authentication);
}
