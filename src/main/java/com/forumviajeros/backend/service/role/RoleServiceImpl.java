package com.forumviajeros.backend.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.forumviajeros.backend.dto.role.RoleRequestDTO;
import com.forumviajeros.backend.dto.role.RoleResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.RoleRepository;
import com.forumviajeros.backend.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        Role saved = roleRepository.save(role);
        return mapToResponseDTO(saved);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        Role updated = roleRepository.save(role);
        return mapToResponseDTO(updated);
    }

    @Override
    public RoleResponseDTO getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
        return mapToResponseDTO(role);
    }

    @Override
    public RoleResponseDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", name));
        return mapToResponseDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
        roleRepository.delete(role);
    }

    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        return user.getId();
    }

    private RoleResponseDTO mapToResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        return dto;
    }
}