package ru.practicum.server.handler.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessException extends RuntimeException {
    private String message;
}
