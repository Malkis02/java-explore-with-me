package ru.practicum.server.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.ListNewUserRequestResp;
import ru.practicum.server.user.dto.NewUserRequest;
import ru.practicum.server.user.dto.NewUserRequestResponse;
import ru.practicum.server.user.dto.UserBlockCommentStatusUpd;
import ru.practicum.server.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerAdmin {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<NewUserRequestResponse> createUser(@Valid @RequestBody NewUserRequest userRequest) {
        log.info("create userRequest: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @GetMapping
    public ResponseEntity<ListNewUserRequestResp> getUsers(@RequestParam(required = false) List<Long> ids,
                                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                           @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("getUsersFromListIds: ids: {}, from: {},size: {}", ids, from, size);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(ids, PageRequest.of(from / size, size)));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long userId) {
        log.info("delete user with id={}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("comments")
    public ResponseEntity<ListNewUserRequestResp> changeCommentBlockedStatus(
            @RequestBody @Valid UserBlockCommentStatusUpd users) {
        log.info("change block status users:{}", users);
        return ResponseEntity.status(HttpStatus.OK).body(userService.changeUserCommentsStatus(users));
    }
}
