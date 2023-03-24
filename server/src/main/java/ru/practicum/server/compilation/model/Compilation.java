package ru.practicum.server.compilation.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compilationId;
    @ManyToMany
    @JoinTable(
            name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")

    )
    private Set<Event> events;
    @Column
    private Boolean pinned = Boolean.FALSE;
    @Column(nullable = false)
    private String title;
}

