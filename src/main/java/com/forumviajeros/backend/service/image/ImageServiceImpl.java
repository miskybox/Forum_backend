package com.forumviajeros.backend.service.image;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.model.Image;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.repository.ImageRepository;
import com.forumviajeros.backend.repository.PostRepository;
import com.forumviajeros.backend.service.storage.StorageService;

@Service
public class ImageServiceImpl implements ImageService {

    private final StorageService storageService;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    public ImageServiceImpl(StorageService storageService, ImageRepository imageRepository,
            PostRepository postRepository) {
        this.storageService = storageService;
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Image saveImage(MultipartFile file, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post no encontrado"));
        String storedFilename = storageService.store(file);
        Image image = new Image();
        image.setPost(post);
        image.setFilePath(storedFilename);
        image.setName(StringUtils.cleanPath(java.util.Objects.requireNonNull(file.getOriginalFilename())));
        image.setType(file.getContentType());
        image.setSize(file.getSize());
        return imageRepository.save(image);
    }

    @Override
    public List<Image> saveImages(List<MultipartFile> files, Long postId) {
        List<Image> savedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            savedImages.add(saveImage(file, postId));
        }
        return savedImages;
    }

    @Override
    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }

    @Override
    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
    }

    @Override
    public List<Image> getImagesByPostId(Long postId) {
        return imageRepository.findByPostId(postId);
    }
}
