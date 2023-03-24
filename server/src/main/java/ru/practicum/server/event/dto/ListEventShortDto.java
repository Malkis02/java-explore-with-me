package ru.practicum.server.event.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ListEventShortDto {
    @JsonValue
    private List<EventShortDto> events;
}
