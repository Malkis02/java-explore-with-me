package ru.practicum.server.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Data
@Builder
@Jacksonized
public class CommentDtoUpdate {
    @Size(min = 1, max = 7000, message = "Invalid comment")
    private String text;
}
