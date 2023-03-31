package ru.practicum.server.request.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ParticipationRequestList {
    @JsonValue
    private List<ParticipationRequestDto> requests;
}
