package ru.practicum.server.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDtoResp {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;

}
