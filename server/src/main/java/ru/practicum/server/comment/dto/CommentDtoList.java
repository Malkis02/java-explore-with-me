package ru.practicum.server.comment.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CommentDtoList {
    @JsonValue
    private List<CommentShortDto> comments;
}
