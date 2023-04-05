package ru.practicum.server.report.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.server.report.dto.ReportDto;
import ru.practicum.server.report.model.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(source = "reportedUser.userId", target = "reportedUser")
    ReportDto mapToReportDto(Report report);
}
