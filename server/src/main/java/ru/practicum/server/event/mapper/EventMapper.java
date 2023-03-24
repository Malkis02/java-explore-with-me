package ru.practicum.server.event.mapper;

import org.mapstruct.*;
import ru.practicum.server.category.mapper.CategoryMapper;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event mapToEvent(NewEventDto eventDto);

    @Mapping(source = "eventId", target = "id")
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "initiator.userId", target = "initiator.id")
    @Mapping(source = "createdOn", target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "publishedOn", target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventFullDto mapToEventFullDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "initiator.userId", target = "initiator.id")
    @Mapping(source = "eventId", target = "id")
    EventShortDto mapToEventShortDto(Event event);

    List<EventShortDto> mapToListEventShortDto(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event mapToEvent(UpdateEventUserRequest updateEvent, @MappingTarget Event event);

    List<EventFullDto> mapToListEventFullDto(List<Event> events);

    State mapToState(String state);

    List<State> mapToListStates(List<String> states);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event mapToEvent(UpdateEventAdminRequest updateEvent, @MappingTarget Event event);
}
