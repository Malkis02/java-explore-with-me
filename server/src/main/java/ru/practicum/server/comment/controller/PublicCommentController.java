package ru.practicum.server.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.comment.dto.CommentDtoList;
import ru.practicum.server.comment.dto.CommentDtoResponse;
import ru.practicum.server.comment.service.CommentService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/comments")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentDtoList> getCommentsPublic(@RequestParam @Min(1) Long eventId) {
        log.info("get comments with eventId={}", eventId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsPublic(eventId));
    }

    @GetMapping("{commentId}")
    public ResponseEntity<CommentDtoResponse> getCommentPublic(@PathVariable @Min(1) Long commentId) {
        log.info("get comment with commentId={}", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentPublic(commentId));
    }
}
