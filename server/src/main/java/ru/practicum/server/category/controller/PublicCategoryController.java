package ru.practicum.server.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.category.dto.ListCategoryDto;
import ru.practicum.server.category.dto.NewCategoryDtoResp;
import ru.practicum.server.category.service.CategoryService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/categories")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ListCategoryDto> getCategories(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("get categories: from: {},size: {}", from, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCategories(PageRequest.of(from / size, size)));
    }

    @GetMapping("{catId}")
    public ResponseEntity<NewCategoryDtoResp> getCategoryById(@PathVariable @Min(1) Long catId) {
        log.info("get category with id={}", catId);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(catId));
    }
}
