package com.forumviajeros.backend.service.role;

import java.util.List;

import com.forumviajeros.backend.dto.role.RoleRequestDTO;
import com.forumviajeros.backend.dto.role.RoleResponseDTO;

public interface RoleService {
    RoleResponseDTO createRole(RoleRequestDTO roleDTO);

    RoleResponseDTO updateRole(Long id, RoleRequestDTO roleDTO);

    RoleResponseDTO getRole(Long id);

    RoleResponseDTO getRoleByName(String name);

    List<RoleResponseDTO> getAllRoles();

    void deleteRole(Long id);
}
