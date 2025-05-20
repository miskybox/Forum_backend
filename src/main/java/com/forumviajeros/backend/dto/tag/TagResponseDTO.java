package com.forumviajeros.backend.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDTO {
    private Long id;
    private String name;
    private int forumCount;
    private int postCount;

}