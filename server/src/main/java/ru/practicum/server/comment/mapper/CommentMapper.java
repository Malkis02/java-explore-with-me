package ru.practicum.server.comment.mapper;

import org.mapstruct.*;
import ru.practicum.server.comment.dto.CommentDtoResponse;
import ru.practicum.server.comment.dto.CommentDtoUpdate;
import ru.practicum.server.comment.dto.CommentShortDto;
import ru.practicum.server.comment.dto.NewCommentDto;
import ru.practicum.server.comment.model.Comment;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment mapToComment(NewCommentDto newComment);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDtoResponse mapToCommentResponse(Comment comment);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentShortDto mapToCommentShortDto(Comment comment);

    Set<CommentShortDto> mapToSetCommentShort(Set<Comment> comments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment mapToComment(CommentDtoUpdate updateComment, @MappingTarget Comment comment);
}
