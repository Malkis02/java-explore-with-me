package ru.practicum.server.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.category.repository.CategoryRepository;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.enums.StateAction;
import ru.practicum.server.event.mapper.EventMapper;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.QEvent;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.event.statclient.StatisticClient;
import ru.practicum.server.handler.exception.AccessException;
import ru.practicum.server.handler.exception.EventStateException;
import ru.practicum.server.handler.exception.NotFoundException;
import ru.practicum.server.request.dto.EventRequestStatusUpdate;
import ru.practicum.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.request.dto.ParticipationRequestList;
import ru.practicum.server.request.enums.RequestStatus;
import ru.practicum.server.request.mapper.RequestMapper;
import ru.practicum.server.request.repository.RequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImp implements EventService {
    private final EventRepository events;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final EventMapper mapper;
    private final RequestMapper requestMapper;
    private final StatisticClient statisticClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestRepository requestRepository;

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto eventDto) {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categories.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=" + eventDto.getCategory() + " was not found"));
        Event newEvent = mapper.mapToEvent(eventDto);
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().minusHours(2))) {
            throw new AccessException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + eventDto.getEventDate());
        } else {
            newEvent.setInitiator(user);
            newEvent.setCategory(category);
            newEvent.setCreatedOn(LocalDateTime.now());
            return mapper.mapToEventFullDto(events.save(newEvent));
        }
    }

    @Override
    public ListEventShortDto getPrivateUserEvents(Long userId, Pageable pageable) {
        if (users.existsById(userId)) {
            return ListEventShortDto
                    .builder()
                    .events(mapper.mapToListEventShortDto(events.findAllByInitiatorUserId(userId, pageable)))
                    .build();
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public EventFullDto getPrivateUserEvent(Long userId, Long eventId) {
        if (users.existsById(userId)) {
            return mapper.mapToEventFullDto(events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found")));
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        if (users.existsById(userId)) {
            LocalDateTime eventTime;
            if (updateEvent.getEventDate() != null) {
                eventTime = LocalDateTime.parse(updateEvent.getEventDate(), formatter);
                if (eventTime.isBefore(LocalDateTime.now().minusHours(2))) {
                    throw new AccessException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                            "Value: " + eventTime);
                }
            }
            Event event = events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
            if (event.getState().equals(State.PUBLISHED)) {
                throw new AccessException("Only pending or canceled events can be changed");
            }
            if (updateEvent.getCategory() != null) {
                event.setCategory(categories.findById(updateEvent.getCategory()).orElseThrow(
                        () -> new NotFoundException("Category with id=" + updateEvent.getCategory() + " was not found")));
            }
            event.setState(StateAction.getState(updateEvent.getStateAction()));
            return mapper.mapToEventFullDto(events.save(mapper.mapToEvent(updateEvent, event)));
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public ListEventFullDto getEventsByFiltersForAdmin(List<Long> ids, List<String> states, List<Long> categories,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        BooleanBuilder booleanBuilder = createQuery(ids, states, categories, rangeStart, rangeEnd);
        Page<Event> page;
        if (booleanBuilder.getValue() != null) {
            page = events.findAll(booleanBuilder, pageable);
        } else {
            page = events.findAll(pageable);
        }
        return ListEventFullDto
                .builder()
                .events(mapper.mapToListEventFullDto(page.getContent()))
                .build();
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        LocalDateTime eventTime;
        if (updateEvent.getEventDate() != null) {
            eventTime = LocalDateTime.parse(updateEvent.getEventDate(), formatter);
            if (eventTime.isBefore(LocalDateTime.now().minusHours(1))) {
                throw new AccessException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                        "Value: " + eventTime);
            }
        }
        Event event = events.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));
        changeEventState(event, updateEvent.getStateAction());
        if (updateEvent.getCategory() != null) {
            event.setCategory(categories.findById(updateEvent.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=" + updateEvent.getCategory() + " was not found")));
        }
        return mapper.mapToEventFullDto(events.save(mapper.mapToEvent(updateEvent, event)));
    }

    @Override
    public ParticipationRequestList getUserEventRequests(Long userId, Long eventId) {
        if (users.existsById(userId)) {
            Event event = events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
            return ParticipationRequestList
                    .builder()
                    .requests(event.getRequests().stream().map(requestMapper::mapToRequestDto).collect(Collectors.toList()))
                    .build();
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult approveRequests(Long userId, Long eventId, EventRequestStatusUpdate requests) {
        if (users.existsById(userId)) {
            Event event = events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
            if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                throw new AccessException("The participant limit has been reached");
            }
            List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
            moderationRequests(confirmedRequests, rejectedRequests, event, requests);
            return EventRequestStatusUpdateResult
                    .builder()
                    .confirmedRequests(confirmedRequests)
                    .rejectedRequests(rejectedRequests)
                    .build();
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest servlet) {
        statisticClient.postStats(servlet, "ewm-server");
        Event event = events.findByEventIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        event.setViews(statisticClient.getViews(eventId));
        return mapper.mapToEventFullDto(events.save(event));
    }

    private void changeEventState(Event event, String actionState) {
        switch (StateAction.getState(actionState)) {
            case PUBLISHED:
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                } else {
                    throw new EventStateException("Cannot publish the event because it's not in the right state: " +
                            event.getState());
                }
            case CANCELED:
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.CANCELED);
                    break;
                } else {
                    throw new EventStateException("Cannot canceled the event because it's not in the right state: " +
                            event.getState());
                }
        }
    }

    @Override
    public ListEventShortDto getEventsByFiltersPublic(String text, List<Long> categories, Boolean paid,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable,
                                                      Pageable pageable, HttpServletRequest servlet) {
        statisticClient.postStats(servlet, "ewm-server");
        BooleanBuilder booleanBuilder = createQuery(null, null, categories, rangeStart, rangeEnd);
        Page<Event> page;
        if (text != null) {
            booleanBuilder.and(QEvent.event.annotation.likeIgnoreCase(text))
                    .or(QEvent.event.description.likeIgnoreCase(text));
        }
        if (rangeStart == null && rangeEnd == null) {
            booleanBuilder.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable) {
            booleanBuilder.and((QEvent.event.participantLimit.eq(0)))
                    .or(QEvent.event.participantLimit.gt(QEvent.event.confirmedRequests));
        }
        if (paid != null) {
            booleanBuilder.and(QEvent.event.paid.eq(paid));
        }
        if (booleanBuilder.getValue() != null) {
            page = events.findAll(booleanBuilder.getValue(), pageable);
        } else {
            page = events.findAll(pageable);
        }
        return ListEventShortDto
                .builder()
                .events(mapper.mapToListEventShortDto(page.getContent()))
                .build();
    }

    private BooleanBuilder createQuery(List<Long> ids, List<String> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (ids != null && !ids.isEmpty()) {
            booleanBuilder.and(QEvent.event.initiator.userId.in(ids));
        }
        if (states != null && !states.isEmpty()) {
            try {
                booleanBuilder.and(QEvent.event.state.in(states.stream().map(State::valueOf).collect(Collectors.toList())));
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        if (categories != null && !categories.isEmpty()) {
            booleanBuilder.and(QEvent.event.category.categoryId.in(categories));
        }
        if (rangeStart != null) {
            booleanBuilder.and(QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            booleanBuilder.and(QEvent.event.eventDate.before(rangeEnd));
        }
        return booleanBuilder;
    }

    private void moderationRequests(List<ParticipationRequestDto> confirmedRequests,
                                    List<ParticipationRequestDto> rejectedRequests,
                                    Event event, EventRequestStatusUpdate requests) {
        requestRepository.findAllByRequestIdIn(requests.getRequestIds()).stream().peek(r -> {
            if (r.getStatus().equals(RequestStatus.PENDING)) {
                if (event.getParticipantLimit() == 0) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                    if (!event.getRequestModeration()) {
                        r.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    } else {
                        if (requests.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
                            r.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        } else {
                            r.setStatus(RequestStatus.REJECTED);
                        }
                    }
                } else {
                    r.setStatus(RequestStatus.REJECTED);
                }
            } else {
                throw new AccessException("Can only confirm PENDING requests");
            }
        }).map(requestMapper::mapToRequestDto).forEach(r -> {
            if (r.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(r);
            } else {
                rejectedRequests.add(r);
            }
        });
    }
}
