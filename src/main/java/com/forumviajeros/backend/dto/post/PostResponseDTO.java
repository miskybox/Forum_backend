package com.forumviajeros.backend.dto.post;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private Long id;

    private String title;

    private String content;

    private Long forumId;

    private List<String> tags;

    private String status;

    private Long viewCount;

    private Long commentCount;

    private String createdAt;

    private String updatedAt;
}
