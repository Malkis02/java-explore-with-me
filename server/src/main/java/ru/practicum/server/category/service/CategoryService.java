package ru.practicum.server.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.category.dto.ListCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.category.dto.UpdateCategoryDto;

public interface CategoryService {
    NewCategoryDtoResp createCategory(NewCategoryDto newCategoryDto);

    NewCategoryDtoResp updateCategory(UpdateCategoryDto updateCategory, Long catId);

    void deleteCategory(Long catId);

    ListCategoryDto getCategories(Pageable pageable);

    NewCategoryDtoResp getCategoryById(Long catId);
}
