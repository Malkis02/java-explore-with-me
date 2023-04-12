package ru.practicum.server.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@Jacksonized
public class NewCommentDto {
    @Size(min = 1, max = 7000, message = "Invalid comment")
    @NotNull(message = "Field: text. Error: must not be blank. Value: null")
    private String text;
}
