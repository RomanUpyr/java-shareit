package ru.practicum.shareit.gt.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * DTO для передачи данных о вещи.
 * Содержит только те поля, которые необходимы для клиент-серверного взаимодействия.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    /**
     * Уникальный идентификатор вещи.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Краткое название вещи.
     */
    @JsonProperty("name")
    private String name;

    /**
     * Подробное описание вещи и её характеристик.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Статус доступности вещи для аренды (устанавливается владельцем вещи).
     */
    @JsonProperty("available")
    private Boolean available;

    /**
     * Ссылка на запрос, если вещь была добавлена в ответ на запрос другого пользователя.
     * Может быть null, если вещь добавлена без запроса.
     */
    @JsonProperty("requestId")
    private Long requestId;

    /**
     * Идентификатор владельца вещи.
     */
    @JsonProperty("ownerId")
    private Long ownerId;

    /**
     * Последнее бронирование (только для владельца).
     */
    @JsonProperty("lastBooking")
    private BookingInfoDto lastBooking;

    /**
     * Следующее бронирование (только для владельца).
     */
    @JsonProperty("nextBooking")
    private BookingInfoDto nextBooking;

    /**
     * Список комментариев к вещи.
     */
    @JsonProperty("comments")
    private List<CommentDto> comments;

    /**
     * DTO для краткой информации о бронировании.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingInfoDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("bookerId")
        private Long bookerId;

        @JsonProperty("start")
        private String start;

        @JsonProperty("end")
        private String end;
    }
}
