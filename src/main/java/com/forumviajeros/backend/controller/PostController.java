package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.post.PostRequestDTO;
import com.forumviajeros.backend.dto.post.PostResponseDTO;
import com.forumviajeros.backend.service.post.PostService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "API para gestión de publicaciones en foros")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByForum(@PathVariable Long forumId) {
        return ResponseEntity.ok(postService.findByForum(forumId));
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postDTO,
            Authentication authentication) {
        String username = authentication.getName();
        Long userId = postService.getUserIdByUsername(username);
        return new ResponseEntity<>(postService.createPost(postDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id,
            @Valid @RequestBody PostRequestDTO postDTO,
            Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<PostResponseDTO> uploadPostImages(@PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) {
        return ResponseEntity.ok(postService.addImages(id, files, authentication));
    }

    @DeleteMapping("/{postId}/images/{imageId}")
    public ResponseEntity<PostResponseDTO> deletePostImage(@PathVariable Long postId,
            @PathVariable Long imageId,
            Authentication authentication) {
        return ResponseEntity.ok(postService.removeImage(postId, imageId, authentication));
    }

    @GetMapping("/user")
    public ResponseEntity<List<PostResponseDTO>> getCurrentUserPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.findByCurrentUser(authentication));
    }
}