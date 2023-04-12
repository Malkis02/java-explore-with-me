package ru.practicum.server.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.category.dto.ListCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.category.dto.UpdateCategoryDto;
import ru.practicum.server.category.mapper.CategoryMapper;
import ru.practicum.server.category.repository.CategoryRepository;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.handler.exception.AccessException;
import ru.practicum.server.handler.exception.NotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categories;

    private final EventRepository events;

    private final CategoryMapper mapper;

    @Override
    public NewCategoryDtoResp createCategory(NewCategoryDto newCategoryDto) {
        return mapper.mapToNewCategoryDtoResp(categories.save(mapper.mapToCategory(newCategoryDto)));
    }

    @Override
    public NewCategoryDtoResp updateCategory(UpdateCategoryDto updateCategory, Long catId) {
        if (categories.existsById(catId)) {
            return mapper.mapToNewCategoryDtoResp(categories.save(mapper.mapToCategory(updateCategory)));
        } else {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        List<Event> eventsList = events.findAllByCategory(categories.findById(catId).orElseThrow());
        if (!categories.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        } else {
            if (eventsList.isEmpty()) {
                categories.deleteById(catId);
            } else {
                throw new AccessException("Category is not empty");
            }

        }
    }

    @Override
    @Transactional(readOnly = true)
    public ListCategoryDto getCategories(Pageable pageable) {
        return ListCategoryDto
                .builder()
                .catList(mapper.mapToListCategories(categories.findAll(pageable)))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public NewCategoryDtoResp getCategoryById(Long catId) {
        return mapper.mapToNewCategoryDtoResp(categories.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found")));
    }
}

