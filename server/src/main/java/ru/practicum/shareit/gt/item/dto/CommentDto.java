package ru.practicum.shareit.gt.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO для работы с комментариями.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    /**
     * Уникальный идентификатор комментария.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Текст комментария.
     */
    @JsonProperty("text")
    private String text;

    /**
     * Имя автора комментария.
     */
    @JsonProperty("authorName")
    private String authorName;

    /**
     * Дата и время создания комментария.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("created")
    private LocalDateTime created;

    /**
     * Идентификатор автора комментария.
     */
    @JsonProperty("authorId")
    private Long authorId;

    /**
     * Идентификатор вещи, к которой оставлен комментарий.
     */
    @JsonProperty("itemId")
    private Long itemId;
}
