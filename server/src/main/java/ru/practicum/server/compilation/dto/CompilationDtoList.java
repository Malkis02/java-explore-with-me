package ru.practicum.server.compilation.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CompilationDtoList {
    @JsonValue
    private List<CompilationDtoResp> compilations;
}
