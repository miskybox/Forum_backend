package com.forumviajeros.backend.service.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.post.PostRequestDTO;
import com.forumviajeros.backend.dto.post.PostResponseDTO;
import com.forumviajeros.backend.model.Forum;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.model.Tag;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.ForumRepository;
import com.forumviajeros.backend.repository.ImageRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.repository.TagRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.storage.LocalStorageService;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final LocalStorageService localStorageService;

    public PostServiceImpl(PostRepository postRepository,
            UserRepository userRepository,
            ForumRepository forumRepository,
            TagRepository tagRepository,
            ImageRepository imageRepository,
            LocalStorageService localStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.tagRepository = tagRepository;
        this.imageRepository = imageRepository;
        this.localStorageService = localStorageService;
    }

    @Override
    public PostResponseDTO createPost(PostRequestDTO dto, Long userId) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setUser(userRepository.findById(userId).orElseThrow());
        post.setForum(forumRepository.findById(dto.getForumId()).orElseThrow());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        if (dto.getTags() != null) {
            List<Tag> tags = dto.getTags().stream().map(
                    name -> tagRepository.findByName(name).orElseGet(
                            () -> tagRepository.save(new Tag(null, name, new ArrayList<>(), new ArrayList<>()))))
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        post.setStatus(dto.getStatus() != null ? Post.PostStatus.valueOf(dto.getStatus()) : Post.PostStatus.ACTIVE);

        return mapToResponseDTO(postRepository.save(post));
    }

    @Override
    public PostResponseDTO updatePost(Long id, PostRequestDTO dto) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (dto.getTags() != null) {
            List<Tag> tags = dto.getTags().stream().map(
                    name -> tagRepository.findByName(name).orElseGet(
                            () -> tagRepository.save(new Tag(null, name, new ArrayList<>(), new ArrayList<>()))))
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        if (dto.getStatus() != null) {
            post.setStatus(Post.PostStatus.valueOf(dto.getStatus()));
        }

        return mapToResponseDTO(postRepository.save(post));
    }

    @Override
    public PostResponseDTO findById(Long id) {
        return mapToResponseDTO(postRepository.findById(id).orElseThrow());
    }

    @Override
    public Page<PostResponseDTO> findAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> findByForum(Long forumId) {
        Forum forum = forumRepository.findById(forumId).orElseThrow();
        return postRepository.findByForum(forum).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> findByCurrentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        return postRepository.findByUser(user).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PostResponseDTO addImages(Long postId, List<MultipartFile> files, Authentication authentication) {
        Post post = postRepository.findById(postId).orElseThrow();
        localStorageService.saveImagesToPost(post, files, imageRepository);
        return mapToResponseDTO(post);
    }

    @Override
    public PostResponseDTO removeImage(Long postId, Long imageId, Authentication authentication) {
        Post post = postRepository.findById(postId).orElseThrow();
        var image = imageRepository.findById(imageId).orElseThrow();
        if (!image.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("Imagen no pertenece a esta publicación");
        }
        imageRepository.delete(image);
        return mapToResponseDTO(post);
    }

    @Override
    public void delete(Long id, Authentication authentication) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private PostResponseDTO mapToResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setForumId(post.getForum().getId());
        dto.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        dto.setStatus(post.getStatus().name());
        dto.setViewCount(post.getViewCount());
        dto.setCommentCount((long) (post.getComments() != null ? post.getComments().size() : 0));
        dto.setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null);
        dto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null);
        return dto;
    }
}