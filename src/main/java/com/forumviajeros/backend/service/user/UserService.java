package com.forumviajeros.backend.service.user;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<UserResponseDTO> getAllUsersPaged(Pageable pageable);

    Page<UserResponseDTO> searchUsers(String query, Pageable pageable);

    void deleteUser(Long id);

    UserResponseDTO changePassword(Long id, String currentPassword, String newPassword);

    UserResponseDTO updateUserRoles(Long id, Set<String> roles);

    UserResponseDTO updateUserStatus(Long id, String status, Authentication authentication);
}
