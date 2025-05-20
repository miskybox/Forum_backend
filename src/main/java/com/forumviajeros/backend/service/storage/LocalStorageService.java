package com.forumviajeros.backend.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.model.Image;
import com.forumviajeros.backend.model.Post;
import com.forumviajeros.backend.repository.ImageRepository;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${com.forumviajeros.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            if (originalFilename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + originalFilename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                return filename;
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

    public String getImage(String filename) {
        try {
            Path file = load(filename);
            byte[] bytes = Files.readAllBytes(file);
            String mimeType = Files.probeContentType(file);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:" + mimeType + ";base64," + base64;
        } catch (IOException e) {
            throw new StorageException("No se pudo leer la imagen", e);
        }
    }

    public byte[] getRawBytes(String filename) {
        try {
            Path file = load(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new StorageException("No se pudo obtener la imagen como bytes", e);
        }
    }

    public String getMimeType(String filename) {
        try {
            Path file = load(filename);
            return Files.probeContentType(file);
        } catch (IOException e) {
            throw new StorageException("No se pudo obtener el tipo MIME de la imagen", e);
        }
    }

    public void saveImagesToPost(Post post, List<MultipartFile> files, ImageRepository imageRepository) {
        for (MultipartFile file : files) {
            String filename = this.store(file);
            Image image = new Image();
            image.setPost(post);
            image.setFilePath(filename); // Cambiado de setPath a setFilePath
            image.setName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            image.setType(file.getContentType());
            image.setSize(file.getSize());
            imageRepository.save(image);
        }
    }
}