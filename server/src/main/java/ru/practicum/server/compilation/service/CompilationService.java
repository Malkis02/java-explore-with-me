package ru.practicum.server.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.compilation.dto.CompilationDtoList;
import ru.practicum.server.compilation.dto.CompilationDtoResp;
import ru.practicum.server.compilation.dto.NewCompilationDto;
import ru.practicum.server.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDtoResp addCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    CompilationDtoResp updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);

    CompilationDtoResp getCompilation(Long compId);

    CompilationDtoList getCompilations(Boolean pinned, Pageable pageable);
}
