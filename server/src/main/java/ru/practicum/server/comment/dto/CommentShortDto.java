package ru.practicum.server.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.comment.enums.CommentState;

@Data
@Builder
public class CommentShortDto {
    private String text;
    private String authorName;
    private CommentState state;
    private String created;
}
