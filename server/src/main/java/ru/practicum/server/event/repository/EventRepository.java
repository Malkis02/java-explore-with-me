package ru.practicum.server.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long>, QuerydslPredicateExecutor<Event>, JpaRepository<Event,Long> {
    List<Event> findAllByInitiatorUserId(Long userId, Pageable pageable);

    Optional<Event> findByEventIdAndInitiatorUserId(Long eventId, Long userId);

    Set<Event> findAllByEventIdIn(List<Long> eventIds);

    Optional<Event> findByEventIdAndState(Long eventId, State state);

    List<Event> findAllByCategory(Category category);


}
