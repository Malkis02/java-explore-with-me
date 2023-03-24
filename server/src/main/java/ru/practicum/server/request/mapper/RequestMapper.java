package ru.practicum.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.server.request.dto.ParticipationRequestDto;
import ru.practicum.server.request.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "event.eventId", target = "event")
    @Mapping(source = "requester.userId", target = "requester")
    @Mapping(source = "requestId", target = "id")
    ParticipationRequestDto mapToRequestDto(Request request);

    List<ParticipationRequestDto> mapToRequestDtoList(List<Request> requests);
}

