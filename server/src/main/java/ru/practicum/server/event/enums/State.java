package ru.practicum.server.event.enums;

public enum State {
    PENDING, PUBLISHED, CANCELED;

    public static Boolean validString(String string) {
        return string.equals(PENDING.toString()) || string.equals(PUBLISHED.toString()) ||
                string.equals(CANCELED.toString());
    }
}
