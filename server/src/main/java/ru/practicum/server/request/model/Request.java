package ru.practicum.server.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.request.enums.RequestStatus;
import ru.practicum.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_id", "requester_id"})})
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @Column
    private LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
}
