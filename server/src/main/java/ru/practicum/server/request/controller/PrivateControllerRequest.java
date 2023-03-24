package ru.practicum.server.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.request.dto.ParticipationRequestList;
import ru.practicum.server.request.service.RequestService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivateControllerRequest {
    private final RequestService requestService;

    @PostMapping("{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable @Min(1) Long userId,
                                                                 @RequestParam @Min(1) Long eventId) {
        log.info("create request with userId={} and eventId={}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(userId, eventId));
    }

    @GetMapping("{userId}/requests")
    public ResponseEntity<ParticipationRequestList> getUserRequests(@PathVariable @Min(1) Long userId) {
        log.info("get requests with userId={}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(requestService.getUserRequests(userId));
    }

    @PatchMapping("{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable @Min(1) Long userId,
                                                                 @PathVariable @Min(1) Long requestId) {
        log.info("cancel request with id={} and userId={}", requestId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(requestService.canceledRequest(userId, requestId));
    }
}
