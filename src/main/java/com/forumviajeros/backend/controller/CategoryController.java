package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.forumviajeros.backend.dto.category.CategoryRequestDTO;
import com.forumviajeros.backend.dto.category.CategoryResponseDTO;
import com.forumviajeros.backend.service.category.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "API para gestión de categorías por continentes")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Devuelve todas las categorías (continentes) disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida con éxito")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Devuelve una categoría según su ID")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada con éxito")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría (continente)")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos", content = @Content)
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.create(categoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.update(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría por su ID")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Subir imagen de categoría", description = "Sube una imagen para la categoría")
    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    public ResponseEntity<CategoryResponseDTO> uploadCategoryImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(categoryService.updateImage(id, file));
    }
}