package ru.practicum.server.handler.exception;

public class EventStateException extends RuntimeException {
    public EventStateException(String message) {
        super(message);
    }
}
