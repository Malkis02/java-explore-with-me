package ru.practicum.server.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ListNewUserRequestResp {
    @JsonValue
    private List<NewUserRequestResponse> users;
}
