package com.forumviajeros.backend.dto.forum;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumResponseDTO {
    private Long id;

    private String title;

    private String description;

    private Long categoryId;

    private List<String> tags;

    private String status;

    private Long viewCount;

    private Long postCount;

    private String createdAt;

    private String updatedAt;
}