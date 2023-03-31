package ru.practicum.server.event.enums;

import ru.practicum.server.handler.exception.EventUpdateException;

public enum StateAction {
    SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT;

    public static  State getState(String stateAction) {
        try {
            switch (valueOf(stateAction)) {
                case SEND_TO_REVIEW:
                    return State.PENDING;
                case CANCEL_REVIEW:
                case REJECT_EVENT:
                    return State.CANCELED;
                case PUBLISH_EVENT:
                    return State.PUBLISHED;
                default:
                    throw new EventUpdateException("Event must not be published");
            }
        } catch (IllegalArgumentException e) {
            throw new EventUpdateException("Event must not be published");
        }
    }
}
