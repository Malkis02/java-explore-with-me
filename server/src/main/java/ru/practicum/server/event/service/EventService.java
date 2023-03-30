package ru.practicum.server.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.request.dto.EventRequestStatusUpdate;
import ru.practicum.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.server.request.dto.ParticipationRequestList;

import javax.servlet.http.HttpServletRequest;

public interface EventService {
    EventFullDto addNewEvent(Long userId, NewEventDto eventDto);

    ListEventShortDto getPrivateUserEvents(Long userId, Pageable pageable);

    EventFullDto getPrivateUserEvent(Long userId, Long eventId);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    ListEventFullDto getEventsByFiltersForAdmin(EventAdminRequestDto event, Pageable pageable);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    ParticipationRequestList getUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult approveRequests(Long userId, Long eventId, EventRequestStatusUpdate requests);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest servlet);

    ListEventShortDto getEventsByFiltersPublic(EventPublicRequestDto event,
                                               Pageable pageable, HttpServletRequest servlet);
}
