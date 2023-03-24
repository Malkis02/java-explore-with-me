package ru.practicum.server.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.user.dto.UserShortDto;

@Data
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private NewCategoryDtoResp category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
