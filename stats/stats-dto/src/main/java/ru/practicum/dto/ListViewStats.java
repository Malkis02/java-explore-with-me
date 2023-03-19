package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ListViewStats {
    @JsonValue
    private List<ViewStats> viewStats;
}
