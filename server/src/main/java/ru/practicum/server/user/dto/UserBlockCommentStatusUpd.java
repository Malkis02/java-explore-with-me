package ru.practicum.server.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.user.enums.UserBanAction;

import java.util.List;

@Data
@Builder
public class UserBlockCommentStatusUpd {
    private List<Long> userIds;
    private UserBanAction status;
}
