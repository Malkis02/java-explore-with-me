package ru.practicum.server.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.event.dto.EventFullDto;
import ru.practicum.server.event.dto.ListEventShortDto;
import ru.practicum.server.event.dto.NewEventDto;
import ru.practicum.server.event.dto.UpdateEventUserRequest;
import ru.practicum.server.event.service.EventService;
import ru.practicum.server.request.dto.EventRequestStatusUpdate;
import ru.practicum.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.server.request.dto.ParticipationRequestList;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping("{userId}/events")
    public ResponseEntity<EventFullDto> addEvent(@PathVariable @Min(1) Long userId,
                                                 @RequestBody @Valid NewEventDto eventDto) {
        log.info("Add new event with user id={}: {}", userId, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addNewEvent(userId, eventDto));
    }

    @GetMapping("{userId}/events")
    public ResponseEntity<ListEventShortDto> getUserEvents(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                           @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                           @PathVariable @Min(1) Long userId) {
        log.info("get events with userId={}, from: {},size: {}", userId, from, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getPrivateUserEvents(userId, PageRequest.of(from / size, size)));
    }

    @GetMapping("{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable @Min(1) Long userId,
                                                     @PathVariable @Min(1) Long eventId) {
        log.info("get event with eventId={} and userId={}", eventId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getPrivateUserEvent(userId, eventId));
    }

    @PatchMapping("{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable @Min(1) Long userId,
                                                    @PathVariable @Min(1) Long eventId,
                                                    @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        log.info("update event with eventId={} and userId={} to event:{}", eventId, userId, updateEvent);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventUser(userId, eventId, updateEvent));
    }

    @GetMapping("{userId}/events/{eventId}/requests")
    public ResponseEntity<ParticipationRequestList> getUserEventRequests(@PathVariable @Min(1) Long userId,
                                                                         @PathVariable @Min(1) Long eventId) {
        log.info("get requests for userId={} and eventId={}", userId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getUserEventRequests(userId, eventId));
    }

    @PatchMapping("{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> approveRequests(@PathVariable @Min(1) Long userId,
                                                                          @PathVariable @Min(1) Long eventId,
                                                                          @RequestBody EventRequestStatusUpdate requests) {
        log.info("processing requests for eventId={} and userId={}", eventId, userId);
        log.info("requests:{}", requests);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.approveRequests(userId, eventId, requests));
    }
}
