// Archivo: ForumService.java
package com.forumviajeros.backend.service.forum;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.forum.ForumRequestDTO;
import com.forumviajeros.backend.dto.forum.ForumResponseDTO;

public interface ForumService {
    ForumResponseDTO createForum(ForumRequestDTO forumDTO, Long userId);

    ForumResponseDTO updateForum(Long id, ForumRequestDTO forumDTO);

    ForumResponseDTO getForum(Long id);

    List<ForumResponseDTO> getAllForums();

    Page<ForumResponseDTO> findAll(Pageable pageable);

    ForumResponseDTO findById(Long id);

    Long getUserIdByUsername(String username);

    List<ForumResponseDTO> findByCategory(Long categoryId);

    List<ForumResponseDTO> searchByKeyword(String keyword);

    void delete(Long id, Authentication authentication);

    List<ForumResponseDTO> findByCurrentUser(Authentication authentication);

    ForumResponseDTO updateImage(Long id, MultipartFile file, Authentication authentication);

    void deleteForum(Long id);
}
