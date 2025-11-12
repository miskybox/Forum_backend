package com.forumviajeros.backend.service.forum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.forum.ForumRequestDTO;
import com.forumviajeros.backend.dto.forum.ForumResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.Tag;
import com.forumviajeros.backend.repository.CategoryRepository;
import com.forumviajeros.backend.repository.ForumRepository;
import com.forumviajeros.backend.repository.TagRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.util.HtmlSanitizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForumServiceImpl implements ForumService {
        private final ForumRepository forumRepository;
        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;
        private final TagRepository tagRepository;

        @Override
        @Transactional
        public ForumResponseDTO createForum(ForumRequestDTO forumDTO, Long userId) {
                Forum forum = new Forum();

                forum.setTitle(HtmlSanitizer.stripAllTags(forumDTO.getTitle()));
                forum.setDescription(HtmlSanitizer.sanitizeRichText(forumDTO.getDescription()));

                forum.setCategory(categoryRepository.findById(forumDTO.getCategoryId())
                                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id",
                                                forumDTO.getCategoryId())));

                forum.setUser(userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId)));

        forum.setTags(tagRepository.findByNameIn(sanitizeTagNames(forumDTO.getTags())));
                forum.setStatus(Forum.ForumStatus.ACTIVE);
                forum.setCreatedAt(LocalDateTime.now());
                forum.setUpdatedAt(LocalDateTime.now());

                Forum savedForum = forumRepository.save(forum);
                return mapToResponseDTO(savedForum);
        }

        @Override
        @Transactional
        public ForumResponseDTO updateForum(Long id, ForumRequestDTO forumDTO, Authentication authentication) {
                Forum forum = forumRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Foro", "id", id));

                assertOwnershipOrAdmin(forum, authentication);

                forum.setTitle(HtmlSanitizer.stripAllTags(forumDTO.getTitle()));
                forum.setDescription(HtmlSanitizer.sanitizeRichText(forumDTO.getDescription()));

                forum.setCategory(categoryRepository.findById(forumDTO.getCategoryId())
                                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id",
                                                forumDTO.getCategoryId())));

                forum.setTags(tagRepository.findByNameIn(sanitizeTagNames(forumDTO.getTags())));
                forum.setUpdatedAt(LocalDateTime.now());

                Forum updatedForum = forumRepository.save(forum);
                return mapToResponseDTO(updatedForum);
        }

        @Override
        public ForumResponseDTO getForum(Long id) {
                Forum forum = forumRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Foro", "id", id));
                return mapToResponseDTO(forum);
        }

        @Override
        public List<ForumResponseDTO> getAllForums() {
                return forumRepository.findAll().stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public Page<ForumResponseDTO> findAll(Pageable pageable) {
                return forumRepository.findAll(pageable)
                                .map(this::mapToResponseDTO);
        }

        @Override
        public ForumResponseDTO findById(Long id) {
                return mapToResponseDTO(
                                forumRepository.findById(id)
                                                .orElseThrow(() -> new ResourceNotFoundException("Foro", "id", id)));
        }

        @Override

        public List<ForumResponseDTO> findByCategory(Long categoryId) {
                Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", categoryId));

                return forumRepository.findByCategory(category).stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ForumResponseDTO> searchByKeyword(String keyword) {
                String sanitizedKeyword = keyword.replaceAll("<[^>]*>", "");

                return forumRepository.searchByKeyword(sanitizedKeyword, Pageable.unpaged()).stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(isolation = Isolation.SERIALIZABLE)
        public void delete(Long id, Authentication authentication) {
                Forum forum = forumRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Foro", "id", id));
                assertOwnershipOrAdmin(forum, authentication);
                forumRepository.delete(forum);
        }

        @Override
        public List<ForumResponseDTO> findByCurrentUser(Authentication authentication) {
                String username = authentication.getName();
                return forumRepository.findByUser(
                                userRepository.findByUsername(username)
                                                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username",
                                                                username)))
                                .stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ForumResponseDTO updateImage(Long id, MultipartFile file, Authentication authentication) {
                Forum forum = forumRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Foro", "id", id));

                assertOwnershipOrAdmin(forum, authentication);

                // TODO: implementar subida real con LocalStorageService
                forum.setUpdatedAt(LocalDateTime.now());
                return mapToResponseDTO(forumRepository.save(forum));
        }

        @Override
        @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
        public void deleteForum(Long id) {
                if (!forumRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Foro", "id", id);
                }
                forumRepository.deleteById(id);
        }

        private ForumResponseDTO mapToResponseDTO(Forum forum) {
                ForumResponseDTO response = new ForumResponseDTO();
                response.setId(forum.getId());
                response.setTitle(forum.getTitle());
                response.setDescription(forum.getDescription());
                response.setCategoryId(forum.getCategory().getId());
                response.setTags(forum.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
                response.setStatus(forum.getStatus().name());
                response.setViewCount(forum.getViewCount());
                response.setPostCount((long) forum.getPosts().size());
                response.setCreatedAt(forum.getCreatedAt().toString());
                response.setUpdatedAt(forum.getUpdatedAt() != null ? forum.getUpdatedAt().toString() : null);
                return response;
        }

        @Override
        public Long getUserIdByUsername(String username) {
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username))
                                .getId();
        }

        private void assertOwnershipOrAdmin(Forum forum, Authentication authentication) {
                if (authentication == null) {
                        throw new org.springframework.security.access.AccessDeniedException("Usuario no autenticado");
                }

                if (isAdmin(authentication)) {
                        return;
                }

                String username = authentication.getName();
                if (!forum.getUser().getUsername().equals(username)) {
                        throw new org.springframework.security.access.AccessDeniedException(
                                        "No tienes permisos para modificar este foro");
                }
        }

        private boolean isAdmin(Authentication authentication) {
                return authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        }

        private List<String> sanitizeTagNames(List<String> tags) {
                if (tags == null) {
                        return Collections.emptyList();
                }

                return tags.stream()
                                .map(HtmlSanitizer::stripAllTags)
                                .map(String::trim)
                                .filter(tag -> !tag.isEmpty())
                                .collect(Collectors.toList());
        }
}
