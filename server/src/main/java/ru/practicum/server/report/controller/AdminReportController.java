package ru.practicum.server.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.report.dto.ReportDto;
import ru.practicum.server.report.service.ReportService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/admin/reports")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminReportController {
    private final ReportService reportService;

    @GetMapping("users/{userId}")
    public ResponseEntity<ReportDto> getReport(@PathVariable @Min(1) Long userId) {
        log.info("get report with userId={}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReportByUserId(userId));
    }
}
