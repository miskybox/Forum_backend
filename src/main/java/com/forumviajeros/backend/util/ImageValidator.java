package com.forumviajeros.backend.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.service.storage.StorageException;

/**
 * Image file validator using magic bytes (file signatures)
 *
 * This validator checks the actual file content (magic bytes) rather than
 * just trusting the file extension or MIME type, which can be easily spoofed.
 *
 * Security: Prevents uploading malicious files disguised as images
 * (e.g., PHP shell with .jpg extension)
 *
 * @see <a href="https://en.wikipedia.org/wiki/List_of_file_signatures">File Signatures</a>
 */
public class ImageValidator {

    // Magic bytes (file signatures) for supported image formats
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] WEBP_MAGIC = {0x52, 0x49, 0x46, 0x46}; // "RIFF" header
    private static final byte[] WEBP_SIGNATURE = {0x57, 0x45, 0x42, 0x50}; // "WEBP" at offset 8

    // Maximum bytes to read for validation (PNG has longest signature at 8 bytes)
    private static final int MAX_SIGNATURE_LENGTH = 12;

    private ImageValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates that the uploaded file is a genuine image by checking magic bytes.
     *
     * @param file the multipart file to validate
     * @throws StorageException if the file is not a valid image
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("El archivo está vacío");
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] headerBytes = new byte[MAX_SIGNATURE_LENGTH];
            int bytesRead = inputStream.read(headerBytes);

            if (bytesRead < 4) {
                throw new StorageException("Archivo demasiado pequeño para ser una imagen válida");
            }

            ImageType detectedType = detectImageType(headerBytes);

            if (detectedType == ImageType.UNKNOWN) {
                throw new StorageException(
                    "El archivo no es una imagen válida. Solo se permiten JPEG, PNG y WebP");
            }

            // Validate that detected type matches declared MIME type
            validateMimeTypeMatch(file.getContentType(), detectedType);

        } catch (IOException e) {
            throw new StorageException("Error al leer el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Detects the actual image type by reading magic bytes.
     *
     * @param headerBytes the first bytes of the file
     * @return the detected image type
     */
    private static ImageType detectImageType(byte[] headerBytes) {
        // Check JPEG (FF D8 FF)
        if (headerBytes.length >= 3 && startsWith(headerBytes, JPEG_MAGIC)) {
            return ImageType.JPEG;
        }

        // Check PNG (89 50 4E 47 0D 0A 1A 0A)
        if (headerBytes.length >= 8 && startsWith(headerBytes, PNG_MAGIC)) {
            return ImageType.PNG;
        }

        // Check WebP (RIFF....WEBP)
        if (headerBytes.length >= 12 &&
            startsWith(headerBytes, WEBP_MAGIC) &&
            matchesAtOffset(headerBytes, WEBP_SIGNATURE, 8)) {
            return ImageType.WEBP;
        }

        return ImageType.UNKNOWN;
    }

    /**
     * Validates that the MIME type matches the detected file type.
     *
     * @param mimeType the declared MIME type
     * @param detectedType the type detected from magic bytes
     * @throws StorageException if there's a mismatch
     */
    private static void validateMimeTypeMatch(String mimeType, ImageType detectedType) {
        if (mimeType == null) {
            throw new StorageException("Tipo MIME no especificado");
        }

        boolean valid = switch (detectedType) {
            case JPEG -> mimeType.equals("image/jpeg") || mimeType.equals("image/jpg");
            case PNG -> mimeType.equals("image/png");
            case WEBP -> mimeType.equals("image/webp");
            case UNKNOWN -> false;
        };

        if (!valid) {
            throw new StorageException(
                String.format(
                    "Tipo de archivo inconsistente. MIME declarado: %s, tipo real detectado: %s",
                    mimeType,
                    detectedType.name()
                )
            );
        }
    }

    /**
     * Checks if the byte array starts with the given signature.
     */
    private static boolean startsWith(byte[] data, byte[] signature) {
        if (data.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (data[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the byte array contains the signature at a specific offset.
     */
    private static boolean matchesAtOffset(byte[] data, byte[] signature, int offset) {
        if (data.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (data[offset + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Enum representing supported image types.
     */
    private enum ImageType {
        JPEG,
        PNG,
        WEBP,
        UNKNOWN
    }
}
