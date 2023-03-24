package ru.practicum.server.compilation.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotNull(message = "Field: title. Error: must not be blank. Value: null")
    private String title;
}
