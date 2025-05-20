package com.forumviajeros.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.service.storage.LocalStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "API para gestión de imágenes")
public class ImageController {

    private final LocalStorageService localStorageService;

    @GetMapping("/{fileName}")
    @Operation(summary = "Obtener imagen por nombre de archivo", description = "Devuelve una imagen almacenada en el sistema")
    @ApiResponse(responseCode = "200", description = "Imagen encontrada con éxito")
    @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    public ResponseEntity<String> getImage(@PathVariable String fileName) {
        String imageDataUrl = localStorageService.getImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(imageDataUrl);
    }
}