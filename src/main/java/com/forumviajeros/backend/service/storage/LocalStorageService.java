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
import java.util.Map;
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
import com.forumviajeros.backend.util.ImageValidator;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;
    private static final Map<String, String> CONTENT_TYPE_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

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

            validateContentType(file);

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String filename = UUID.randomUUID().toString() + resolveExtension(file, originalFilename);

            if (originalFilename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + originalFilename);
            }

            Files.createDirectories(this.rootLocation);

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
        // Security: Validate filename to prevent path traversal attacks
        validateFilename(filename);
        Path resolvedPath = rootLocation.resolve(filename).normalize();

        // Ensure the resolved path is still within the root location
        if (!resolvedPath.startsWith(rootLocation)) {
            throw new StorageException("Cannot access file outside of storage directory");
        }

        return resolvedPath;
    }

    /**
     * Security: Validates filename to prevent path traversal attacks
     * Only allows alphanumeric characters, hyphens, underscores, and dots
     * Must match UUID pattern followed by extension
     */
    private void validateFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new StorageException("Filename cannot be empty");
        }

        // Check for path traversal attempts
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new StorageException("Invalid filename: path traversal not allowed");
        }

        // Validate filename format (UUID + extension)
        if (!filename.matches("^[a-fA-F0-9\\-]+\\.(jpg|jpeg|png|webp)$")) {
            throw new StorageException("Invalid filename format");
        }
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
            validateContentType(file);
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

    private void validateContentType(MultipartFile file) {
        // First, validate MIME type (basic check)
        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPE_EXTENSION.containsKey(contentType)) {
            throw new StorageException("Tipo de archivo no permitido: " + contentType);
        }

        // Second, validate actual file content using magic bytes (security check)
        // This prevents uploading malicious files with spoofed extensions/MIME types
        ImageValidator.validateImageFile(file);
    }

    private String resolveExtension(MultipartFile file, String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (StringUtils.hasText(extension)) {
            return "." + extension;
        }

        String contentType = file.getContentType();
        if (contentType != null && CONTENT_TYPE_EXTENSION.containsKey(contentType)) {
            return CONTENT_TYPE_EXTENSION.get(contentType);
        }

        throw new StorageException("No se pudo determinar la extensión del archivo subido");
    }
}