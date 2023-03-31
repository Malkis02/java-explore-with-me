package ru.practicum.server.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.server.category.dto.NewCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.category.dto.UpdateCategoryDto;
import ru.practicum.server.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category mapToCategory(NewCategoryDto newCategoryDto);

    @Mapping(source = "categoryId", target = "id")
    NewCategoryDtoResp mapToNewCategoryDtoResp(Category category);

    Category mapToCategory(UpdateCategoryDto updateCategory);

    List<NewCategoryDtoResp> mapToListCategories(Page<Category> page);
}
