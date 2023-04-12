package ru.practicum.server.event.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.event.enums.State;
import ru.practicum.server.event.location.Location;
import ru.practicum.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    @Column(nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Embedded
    private Location location;
    @Column
    private Boolean paid = Boolean.FALSE;
    @Column(name = "participant_limit")
    private Integer participantLimit = 0;
    @Column(name = "request_moderation")
    private Boolean requestModeration = Boolean.TRUE;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator")
    private User initiator;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;
}
