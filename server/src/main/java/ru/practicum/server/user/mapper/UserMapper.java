package ru.practicum.server.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.server.user.dto.NewUserRequest;
import ru.practicum.server.user.dto.NewUserRequestResponse;
import ru.practicum.server.user.dto.UserShortDto;
import ru.practicum.server.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User mapToUser(NewUserRequest userRequest);

    @Mapping(source = "userId", target = "id")
    NewUserRequestResponse mapToUserRequestResp(User user);

    List<NewUserRequestResponse> mapToUserRequestResp(Page<User> page);

    UserShortDto mapToUserShortDto(User user);
}
