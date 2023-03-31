package ru.practicum.server.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.compilation.dto.CompilationDtoList;
import ru.practicum.server.compilation.dto.CompilationDtoResp;
import ru.practicum.server.compilation.service.CompilationService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("{compId}")
    public ResponseEntity<CompilationDtoResp> getCompilation(@PathVariable @Min(1) Long compId) {
        log.info("get compilation with id={}", compId);
        return ResponseEntity.status(HttpStatus.OK).body(compilationService.getCompilation(compId));
    }

    @GetMapping
    public ResponseEntity<CompilationDtoList> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                              @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                              @RequestParam(defaultValue = "0") @Min(0) Integer from) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(compilationService.getCompilations(pinned, PageRequest.of(from / size, size)));
    }
}

