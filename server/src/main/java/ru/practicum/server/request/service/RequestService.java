package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.request.dto.ParticipationRequestList;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestList getUserRequests(Long userId);

    ParticipationRequestDto canceledRequest(Long userId, Long eventId);
}
