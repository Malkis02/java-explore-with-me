package ru.practicum.server.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.request.enums.RequestStatus;

@Data
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private RequestStatus status;
    private String created;
}
