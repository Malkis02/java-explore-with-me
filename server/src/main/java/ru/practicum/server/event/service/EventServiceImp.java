package ru.practicum.server.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.category.repository.CategoryRepository;
import ru.practicum.server.comment.mapper.CommentMapper;
import ru.practicum.server.comment.repository.CommentRepository;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.enums.StateAction;
import ru.practicum.server.event.mapper.EventMapper;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.QEvent;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.client.statclient.StatisticClient;
import ru.practicum.server.handler.exception.AccessException;
import ru.practicum.server.handler.exception.EventStateException;
import ru.practicum.server.handler.exception.NotFoundException;
import ru.practicum.server.request.dto.EventRequestStatusUpdate;
import ru.practicum.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.request.dto.ParticipationRequestList;
import ru.practicum.server.request.enums.RequestStatus;
import ru.practicum.server.request.mapper.RequestMapper;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.request.repository.RequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ComponentScan("ru.practicum.client.statclient")
public class EventServiceImp implements EventService {
    private final EventRepository events;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final CommentRepository commentRepository;
    private final EventMapper mapper;
    private final CommentMapper commentMapper;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto eventDto) {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categories.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=" + eventDto.getCategory() + " was not found"));
        Event newEvent = mapper.mapToEvent(eventDto);
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
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
            EventFullDto fullDto = mapper.mapToEventFullDto(events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found")));
            fullDto.setViews(statisticClient.getViews(eventId));
            fullDto.setComments(commentMapper.mapToListCommentShort(commentRepository.findAllByEventEventIdAndAuthorUserId(eventId,userId)));
            return fullDto;
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
                throw new AccessException("The event cannot be updated because it has already been published");
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
    public ListEventFullDto getEventsByFiltersForAdmin(EventAdminRequestDto event, Pageable pageable) {
        BooleanBuilder booleanBuilder = createQuery(event.getUsers(), event.getStates(), event.getCategories(),
                event.getRangeStart(), event.getRangeEnd());
        Page<Event> page;
        if (booleanBuilder.getValue() != null) {
            page = events.findAll(booleanBuilder, pageable);
        } else {
            page = events.findAll(pageable);
        }
        List<Long> eventsId = events.findAll()
                .stream()
                .map(Event::getEventId)
                .collect(Collectors.toList());
        ListEventFullDto fullDto = ListEventFullDto
                .builder()
                .events(mapper.mapToListEventFullDto(page.getContent()))
                .build();
        List<ViewStats> viewStats = statisticClient.getViews(eventsId);
        Map<Long,ViewStats> views = new HashMap<>();
        for (ViewStats stat:viewStats) {
            views.put(Long.parseLong(stat.getUri().replace("/events/", "")),stat);
        }
        for (EventFullDto eventFull: fullDto.getEvents()) {
            if (views.containsKey(eventFull.getId())) {
                eventFull.setViews(views.get(eventFull.getId()).getHits());
                eventFull.setConfirmedRequests(requestRepository.findAllByEventEventId(eventFull.getId())
                        .stream()
                        .map(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                        .count());
            } else {
                eventFull.setViews(0L);
            }
        }
        return fullDto;
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
                    .requests(requestRepository.findAllByEvent(event).stream().map(requestMapper::mapToRequestDto).collect(Collectors.toList()))
                    .build();
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult approveRequests(Long userId, Long eventId,
                                                                   EventRequestStatusUpdate eventDto) {
        Event event = events.findByEventIdAndInitiatorUserId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getUserId().equals(userId))
            throw new NotFoundException(String.format("User with id = %d not initiator of event with id = %d", userId, eventId));
        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            return EventRequestStatusUpdateResult
                    .builder()
                    .build();
        }

        var countConfirmedReq = requestRepository.findAllByEvent(event)
                .stream()
                .filter(r -> r.getStatus().equals(RequestStatus.CONFIRMED))
                .count();
        if (countConfirmedReq >= event.getParticipantLimit())
            throw new AccessException("The participant limit has been reached");

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByEvent(event).stream()
                .filter(o -> eventDto.getRequestIds().contains(o.getRequestId()))
                .collect(Collectors.toList());
        Set<Long> requestsId = requests.stream()
                .map(Request::getRequestId)
                .collect(Collectors.toSet());
        eventDto.getRequestIds().forEach(o -> {
            if (!requestsId.contains(o)) {
                throw new NotFoundException(String.format("Request with id = %d not found", o));
            }
        });

        switch (eventDto.getStatus()) {
            case "CONFIRMED":
                for (Request request : requests) {
                    checkRequestStatus(request);
                    if (countConfirmedReq < event.getParticipantLimit()) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        confirmedRequests.add(requestMapper.mapToRequestDto(request));
                        countConfirmedReq++;
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(requestMapper.mapToRequestDto(request));
                    }
                }
                break;
            case "REJECTED":
                for (Request request : requests) {
                    checkRequestStatus(request);
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.mapToRequestDto(request));
                }
        }
        return  EventRequestStatusUpdateResult
                .builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest servlet) {
        statisticClient.postStats(servlet, "ewm-server");
        Event event = events.findByEventIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        EventFullDto eventDto = mapper.mapToEventFullDto(event);
        eventDto.setViews(statisticClient.getViews(eventId));
        return eventDto;
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
    public ListEventShortDto getEventsByFiltersPublic(EventPublicRequestDto event,
                                                      Pageable pageable, HttpServletRequest servlet) {
        statisticClient.postStats(servlet, "ewm-server");
        BooleanBuilder booleanBuilder = createQuery(null, null, event.getCategories(), event.getRangeStart(),
                event.getRangeEnd());
        Page<Event> page;
        if (event.getText() != null) {
            booleanBuilder.and(QEvent.event.annotation.likeIgnoreCase(event.getText()))
                    .or(QEvent.event.description.likeIgnoreCase(event.getText()));
        }
        if (event.getRangeStart() == null && event.getRangeEnd() == null) {
            booleanBuilder.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
        if (event.getOnlyAvailable()) {
            booleanBuilder.and((QEvent.event.participantLimit.eq(0)))
                    .or(QEvent.event.participantLimit.gt(QEvent.event.confirmedRequests));
        }
        if (event.getPaid() != null) {
            booleanBuilder.and(QEvent.event.paid.eq(event.getPaid()));
        }
        if (booleanBuilder.getValue() != null) {
            page = events.findAll(booleanBuilder.getValue(), pageable);
        } else {
            page = events.findAll(pageable);
        }
        List<Long> eventsId = events.findAll()
                .stream()
                .map(Event::getEventId)
                .collect(Collectors.toList());
        ListEventShortDto shortDto = ListEventShortDto
                .builder()
                .events(mapper.mapToListEventShortDto(page.getContent()))
                .build();
        List<ViewStats> viewStats = statisticClient.getViews(eventsId);
        Map<Long,ViewStats> views = new HashMap<>();
        for (ViewStats stat:viewStats) {
            views.put(Long.parseLong(stat.getUri().replace("/events/", "")),stat);
        }
        for (EventShortDto eventShort: shortDto.getEvents()) {
            if (views.containsKey(eventShort.getId())) {
                eventShort.setViews(views.get(eventShort.getId()).getHits());
                eventShort.setConfirmedRequests(requestRepository.findAllByEventEventId(eventShort.getId())
                        .stream()
                        .map(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                        .count());
            } else {
             eventShort.setViews(0L);
            }
        }
        return shortDto;

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

    private void checkRequestStatus(Request request) {
        if (request.getStatus().equals(RequestStatus.CONFIRMED))
            throw new AccessException(String.format(
                    "Request with id = %d has already been published", request.getRequestId()));
        if (request.getStatus().equals(RequestStatus.REJECTED))
            throw new AccessException(String.format(
                    "Request with id = %d has been rejected and cannot be published", request.getRequestId()));
        if (request.getStatus().equals(RequestStatus.CANCELED))
            throw new AccessException(String.format(
                    "Request with id = %d has been canceled and cannot be published", request.getRequestId()));
    }
}
