package ru.practicum.server.report.service;

import ru.practicum.server.report.dto.ReportDto;

public interface ReportService {
    ReportDto getReportByUserId(Long userId);
}
