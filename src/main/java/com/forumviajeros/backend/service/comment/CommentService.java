package com.forumviajeros.backend.service.comment;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.forumviajeros.backend.dto.comment.CommentRequestDTO;
import com.forumviajeros.backend.dto.comment.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO createComment(CommentRequestDTO commentDTO, Authentication authentication, Long postId);

    CommentResponseDTO updateComment(Long id, CommentRequestDTO commentDTO, Authentication authentication);

    CommentResponseDTO getComment(Long id);

    List<CommentResponseDTO> getAllComments();

    List<CommentResponseDTO> getCommentsByPost(Long postId);

    void deleteComment(Long id, Authentication authentication);

    CommentResponseDTO updateCommentStatus(Long id, String status, Authentication authentication);
}