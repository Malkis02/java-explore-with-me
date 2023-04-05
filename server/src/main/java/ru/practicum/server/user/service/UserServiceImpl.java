package ru.practicum.server.user.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.handler.exception.NotFoundException;
import ru.practicum.server.user.dto.ListNewUserRequestResp;
import ru.practicum.server.user.dto.NewUserRequest;
import ru.practicum.server.user.dto.NewUserRequestResponse;
import ru.practicum.server.user.dto.UserBlockCommentStatusUpd;
import ru.practicum.server.user.enums.UserBanAction;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.QUser;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository usersRepository;
    private final UserMapper mapper;

    @Override
    public NewUserRequestResponse createUser(NewUserRequest userRequest) {
        return mapper.mapToUserRequestResp(usersRepository.save(mapper.mapToUser(userRequest)));
    }

    @Override
    public ListNewUserRequestResp getUsers(List<Long> ids, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (ids != null && !ids.isEmpty()) {
            booleanBuilder.and(QUser.user.userId.in(ids));
        }
        Page<User> page;
        if (booleanBuilder.getValue() != null) {
            page = usersRepository.findAll(booleanBuilder.getValue(), pageable);
        } else {
            page = usersRepository.findAll(pageable);
        }
        return ListNewUserRequestResp
                .builder()
                .users(mapper.mapToUserRequestResp(page))
                .build();
    }

    @Override
    public void deleteUser(Long userId) {
        if (usersRepository.existsById(userId)) {
            usersRepository.deleteById(userId);
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    @Transactional
    public ListNewUserRequestResp changeUserCommentsStatus(UserBlockCommentStatusUpd users) {
        List<NewUserRequestResponse> response = usersRepository.findAllByUserIdIn(users.getUserIds()).stream().peek(u -> {
            if (users.getStatus().equals(UserBanAction.BANNED)) {
                u.setAreCommentsBlocked(Boolean.TRUE);
            }
            if (users.getStatus().equals(UserBanAction.UNBANNED)) {
                u.setAreCommentsBlocked(Boolean.FALSE);
            }
        }).map(mapper::mapToUserRequestResp).collect(Collectors.toList());
        return ListNewUserRequestResp
                .builder()
                .users(response)
                .build();
    }
}
