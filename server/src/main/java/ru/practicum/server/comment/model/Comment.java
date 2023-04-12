package ru.practicum.server.comment.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.practicum.server.comment.enums.CommentState;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    @Column(nullable = false)
    private String text;
    @Enumerated(EnumType.STRING)
    private CommentState state = CommentState.NOT_EDIT;
    @Column(nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return commentId != null && Objects.equals(commentId, comment.commentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
