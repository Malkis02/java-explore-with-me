package ru.practicum.server.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.handler.exception.NotFoundException;
import ru.practicum.server.report.dto.ReportDto;
import ru.practicum.server.report.mapper.ReportMapper;
import ru.practicum.server.report.repository.ReportRepository;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class ReportServiceImp implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ReportDto getReportByUserId(Long userId) {
        return mapper.mapToReportDto(reportRepository.findByReportedUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("Report with reported user id=" + userId + " not found")));
    }
}
