package ru.practicum.server.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {
    Long reportId;
    Long reportedUser;
    String reportedMessage;
}
