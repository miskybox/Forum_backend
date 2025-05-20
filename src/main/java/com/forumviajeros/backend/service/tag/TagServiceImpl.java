package com.forumviajeros.backend.service.tag;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.forumviajeros.backend.dto.tag.TagRequestDTO;
import com.forumviajeros.backend.dto.tag.TagResponseDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Tag;
import com.forumviajeros.backend.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagResponseDTO createTag(TagRequestDTO tagDTO) {
        if (tagRepository.findByName(tagDTO.getName()).isPresent()) {
            throw new RuntimeException("La etiqueta ya existe");
        }

        Tag tag = new Tag();
        tag.setName(tagDTO.getName());

        Tag savedTag = tagRepository.save(tag);
        return mapToResponseDTO(savedTag);
    }

    @Override
    public TagResponseDTO updateTag(Long id, TagRequestDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta", "id", id));

        if (!tag.getName().equals(tagDTO.getName()) && tagRepository.findByName(tagDTO.getName()).isPresent()) {
            throw new RuntimeException("Ya existe una etiqueta con ese nombre");
        }

        tag.setName(tagDTO.getName());
        Tag updatedTag = tagRepository.save(tag);
        return mapToResponseDTO(updatedTag);
    }

    @Override
    public TagResponseDTO getTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta", "id", id));
        return mapToResponseDTO(tag);
    }

    @Override
    public TagResponseDTO getTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta", "nombre", name));
        return mapToResponseDTO(tag);
    }

    @Override
    public List<TagResponseDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll(Sort.by("name"));
        return tags.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagResponseDTO> getMostUsedTags(int limit) {
        List<Tag> tags = tagRepository.findAll(
                PageRequest.of(0, limit, Sort.by("name")))
                .getContent();

        return tags.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta", "id", id));

        if (!tag.getForums().isEmpty() || !tag.getPosts().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la etiqueta porque está en uso");
        }

        tagRepository.delete(tag);
    }

    private TagResponseDTO mapToResponseDTO(Tag tag) {
        return new TagResponseDTO(
                tag.getId(),
                tag.getName(),
                tag.getForums() != null ? tag.getForums().size() : 0,
                tag.getPosts() != null ? tag.getPosts().size() : 0);
    }
}
