package ru.practicum.server.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.server.compilation.dto.CompilationDtoResp;
import ru.practicum.server.compilation.dto.NewCompilationDto;
import ru.practicum.server.compilation.dto.UpdateCompilationRequest;
import ru.practicum.server.compilation.model.Compilation;
import ru.practicum.server.event.mapper.EventMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    Compilation mapToCompilation(NewCompilationDto compilationDto);

    @Mapping(target = "id", source = "compilationId")
    CompilationDtoResp mapToCompilationResp(Compilation compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    Compilation mapToCompilation(UpdateCompilationRequest updateCompilation, @MappingTarget Compilation compilation);

    List<CompilationDtoResp> mapToCompilationRespList(List<Compilation> compilations);
}
