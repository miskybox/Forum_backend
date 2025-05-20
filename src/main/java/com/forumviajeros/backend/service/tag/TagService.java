package com.forumviajeros.backend.service.tag;

import java.util.List;

import com.forumviajeros.backend.dto.tag.TagRequestDTO;
import com.forumviajeros.backend.dto.tag.TagResponseDTO;

public interface TagService {
    TagResponseDTO createTag(TagRequestDTO tagDTO);

    TagResponseDTO updateTag(Long id, TagRequestDTO tagDTO);

    TagResponseDTO getTag(Long id);

    TagResponseDTO getTagByName(String name);

    List<TagResponseDTO> getAllTags();

    List<TagResponseDTO> getMostUsedTags(int limit);

    void deleteTag(Long id);
}
