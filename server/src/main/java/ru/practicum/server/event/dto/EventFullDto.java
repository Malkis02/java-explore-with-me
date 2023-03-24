package ru.practicum.server.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.location.Location;
import ru.practicum.server.user.dto.UserShortDto;


@Data
@Builder
public class EventFullDto {
    private Long id;
    private String annotation;
    private NewCategoryDtoResp category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Location location;
    private UserShortDto initiator;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private State state;
    private String title;
    private Long views;
}
