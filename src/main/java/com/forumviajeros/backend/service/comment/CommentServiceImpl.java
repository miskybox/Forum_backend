package com.forumviajeros.backend.service.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.forumviajeros.backend.dto.comment.CommentRequestDTO;
import com.forumviajeros.backend.dto.comment.CommentResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Comment;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.CommentRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.util.HtmlSanitizer;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
            UserRepository userRepository,
            PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, Authentication authentication,
            Long postId) {
        User user = getUserFromAuthentication(authentication);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación", "id", postId));

        Comment comment = new Comment();
        comment.setContent(HtmlSanitizer.sanitizeRichText(commentRequestDTO.getContent()));
        comment.setUser(user);
        comment.setPost(post);
        comment.setStatus(Comment.CommentStatus.ACTIVE);

        Comment savedComment = commentRepository.save(comment);
        return mapToResponseDTO(savedComment);
    }

    @Override
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO,
            Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", commentId));

        if (!comment.getUser().getId().equals(user.getId()) && !isAdmin(authentication) && !isModerator(authentication)) {
            throw new AccessDeniedException("No tienes permisos para editar este comentario");
        }

        comment.setContent(HtmlSanitizer.sanitizeRichText(commentRequestDTO.getContent()));
        comment.setStatus(Comment.CommentStatus.EDITED);

        Comment updatedComment = commentRepository.save(comment);
        return mapToResponseDTO(updatedComment);
    }

    @Override
    public CommentResponseDTO getComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", id));
        return mapToResponseDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Publicación", "id", postId);
        }

        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", id));

        if (!comment.getUser().getId().equals(user.getId()) && !isAdmin(authentication) && !isModerator(authentication)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este comentario");
        }

        commentRepository.deleteById(id);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    private boolean isModerator(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_MODERATOR"));
    }

    private CommentResponseDTO mapToResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getProfileImageUrl(),
                comment.getStatus().name(),
                comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null,
                comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null);
    }
}
