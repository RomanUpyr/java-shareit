package ru.practicum.shareit.gt.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.item.dto.CommentDto;
import ru.practicum.shareit.gt.item.model.Comment;

/**
 * Маппер для преобразования между Entity и DTO комментариев.
 */
@Component
@RequiredArgsConstructor
public class CommentMapper {
    /**
     * Преобразует Entity комментария в DTO.
     */
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .itemId(comment.getItem().getId())
                .created(comment.getCreated())
                .build();
    }

    /**
     * Преобразует DTO комментария в Entity.
     */
    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated() != null ? commentDto.getCreated() : java.time.LocalDateTime.now())
                .build();
    }
}
