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

import com.forumviajeros.backend.dto.tag.TagRequestDTO;
import com.forumviajeros.backend.dto.tag.TagResponseDTO;
import com.forumviajeros.backend.service.tag.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(@Valid @RequestBody TagRequestDTO tagRequestDTO) {
        return new ResponseEntity<>(tagService.createTag(tagRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TagResponseDTO> getTagByName(@PathVariable String name) {
        return ResponseEntity.ok(tagService.getTagByName(name));
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TagResponseDTO>> getMostUsedTags(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(tagService.getMostUsedTags(limit));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable Long id,
            @Valid @RequestBody TagRequestDTO tagRequestDTO) {
        return ResponseEntity.ok(tagService.updateTag(id, tagRequestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}