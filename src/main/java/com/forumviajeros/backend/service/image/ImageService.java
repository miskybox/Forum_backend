package com.forumviajeros.backend.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.model.Image;

public interface ImageService {

    Image saveImage(MultipartFile file, Long postId);

    List<Image> saveImages(List<MultipartFile> files, Long postId);

    void deleteImage(Long imageId);

    Image getImageById(Long imageId);

    List<Image> getImagesByPostId(Long postId);
}