package ru.practicum.server.category.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.category.model.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
}
