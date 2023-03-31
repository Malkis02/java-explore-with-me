package ru.practicum.server.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewCategoryDtoResp {
    private Long id;
    private String name;
}
