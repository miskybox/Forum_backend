package com.forumviajeros.backend.dto.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {
    private Long id;
    private String name;
    private String type;
    private String filePath;
    private String dataUrl;
    private Long size;
    private Long postId;
}