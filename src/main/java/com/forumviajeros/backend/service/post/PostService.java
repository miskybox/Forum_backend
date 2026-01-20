package com.forumviajeros.backend.service.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.post.PostRequestDTO;
import com.forumviajeros.backend.dto.post.PostResponseDTO;

public interface PostService {
    PostResponseDTO createPost(PostRequestDTO postDTO, Long userId);

    PostResponseDTO updatePost(Long id, PostRequestDTO postDTO, Authentication authentication);

    PostResponseDTO findById(Long id);

    Page<PostResponseDTO> findAll(Pageable pageable);

    List<PostResponseDTO> getAllPosts();

    List<PostResponseDTO> findByForum(Long forumId);

    List<PostResponseDTO> findByCurrentUser(Authentication authentication);

    PostResponseDTO addImages(Long postId, List<MultipartFile> files, Authentication authentication);

    PostResponseDTO removeImage(Long postId, Long imageId, Authentication authentication);

    void delete(Long id, Authentication authentication);

    Long getUserIdByUsername(String username);
}
