package ru.practicum.server.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.event.dto.EventFullDto;
import ru.practicum.server.event.dto.ListEventShortDto;
import ru.practicum.server.event.enums.EventSort;
import ru.practicum.server.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublicEventController {
    private final EventService eventService;

    @GetMapping("{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable("id") @Min(1) Long eventId,
                                                     HttpServletRequest servlet) {
        log.info("get event with id={}", eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventByIdPublic(eventId, servlet));
    }

    @GetMapping
    public ResponseEntity<ListEventShortDto> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest servlet) {
        log.info("get events public");
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getEventsByFiltersPublic(
                        text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                        PageRequest.of(from / size, size, Sort.by(EventSort.getSortField(sort)).ascending()), servlet));
    }
}
