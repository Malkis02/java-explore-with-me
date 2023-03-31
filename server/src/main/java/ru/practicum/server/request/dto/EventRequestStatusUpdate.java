package ru.practicum.server.request.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdate {
    List<Long> requestIds;
    String status;
}
