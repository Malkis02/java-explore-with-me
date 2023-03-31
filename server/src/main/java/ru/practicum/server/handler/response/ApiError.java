package ru.practicum.server.handler.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiError {
    @JsonIgnore
    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
