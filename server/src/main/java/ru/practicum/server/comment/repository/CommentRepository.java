package ru.practicum.server.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.comment.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentIdAndAuthorUserId(Long commentId, Long userId);

    Boolean existsByCommentIdAndAuthorUserId(Long commentId, Long userId);

    List<Comment> findAllByAuthorUserIdAndEventEventId(Long userId, Long eventId);

    List<Comment> findAllByEventEventId(Long eventId);
}
