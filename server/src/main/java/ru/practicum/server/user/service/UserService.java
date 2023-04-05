package ru.practicum.server.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.user.dto.ListNewUserRequestResp;
import ru.practicum.server.user.dto.NewUserRequest;
import ru.practicum.server.user.dto.NewUserRequestResponse;
import ru.practicum.server.user.dto.UserBlockCommentStatusUpd;

import java.util.List;

public interface UserService {
    NewUserRequestResponse createUser(NewUserRequest userRequest);

    ListNewUserRequestResp getUsers(List<Long> ids, Pageable pageable);

    void deleteUser(Long userId);

    ListNewUserRequestResp changeUserCommentsStatus(UserBlockCommentStatusUpd users);
}
