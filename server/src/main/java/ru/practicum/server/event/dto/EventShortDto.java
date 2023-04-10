package ru.practicum.server.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.comment.dto.CommentDtoList;
import ru.practicum.server.comment.dto.CommentShortDto;
import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.user.dto.UserShortDto;

import java.util.List;

@Data
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private NewCategoryDtoResp category;
    private Long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
    private List<CommentShortDto> comments;

}
