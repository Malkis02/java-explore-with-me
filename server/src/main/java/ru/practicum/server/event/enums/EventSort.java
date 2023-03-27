package ru.practicum.server.event.enums;

public enum EventSort {
    EVENT_DATE;

    public static String getSortField(String string) {
        if (string != null && string.equals(EVENT_DATE.toString())) {
            return "eventDate";
        }
        return "eventId";
    }
}
