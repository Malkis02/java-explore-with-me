package ru.practicum.server.compilation.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.compilation.dto.CompilationDtoList;
import ru.practicum.server.compilation.dto.CompilationDtoResp;
import ru.practicum.server.compilation.dto.NewCompilationDto;
import ru.practicum.server.compilation.dto.UpdateCompilationRequest;
import ru.practicum.server.compilation.mapper.CompilationMapper;
import ru.practicum.server.compilation.model.Compilation;
import ru.practicum.server.compilation.model.QCompilation;
import ru.practicum.server.compilation.repository.CompilationRepository;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.handler.exception.NotFoundException;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationServiceImp implements CompilationService {
    private final CompilationRepository compilations;
    private final CompilationMapper mapper;
    private final EventRepository events;

    @Override
    public CompilationDtoResp addCompilation(NewCompilationDto compilationDto) {
        Set<Event> findEvents = events.findAllByEventIdIn(compilationDto.getEvents());
        Compilation compilation = mapper.mapToCompilation(compilationDto);
        compilation.setEvents(findEvents);
        return mapper.mapToCompilationResp(compilations.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (compilations.existsById(compId)) {
            compilations.deleteById(compId);
        } else {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
    }

    @Override
    @Transactional
    public CompilationDtoResp updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = compilations.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        Set<Event> findEvents = events.findAllByEventIdIn(updateCompilation.getEvents());
        compilation = mapper.mapToCompilation(updateCompilation, compilation);
        compilation.setEvents(findEvents);
        return mapper.mapToCompilationResp(compilations.save(compilation));
    }

    @Override
    public CompilationDtoResp getCompilation(Long compId) {
        return mapper.mapToCompilationResp(compilations.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }

    @Override
    public CompilationDtoList getCompilations(Boolean pinned, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        Page<Compilation> page;
        if (pinned != null) {
            booleanBuilder.and(QCompilation.compilation.pinned.eq(pinned));
        }
        if (booleanBuilder.getValue() != null) {
            page = compilations.findAll(booleanBuilder.getValue(), pageable);
        } else {
            page = compilations.findAll(pageable);
        }
        return CompilationDtoList
                .builder()
                .compilations(mapper.mapToCompilationRespList(page.getContent()))
                .build();
    }
}
