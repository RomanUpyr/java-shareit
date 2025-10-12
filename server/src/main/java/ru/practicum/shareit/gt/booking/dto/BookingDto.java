package ru.practicum.shareit.gt.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.gt.item.dto.ItemDto;
import ru.practicum.shareit.gt.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * DTO для работы с бронированиями.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    /**
     * Уникальный идентификатор бронирования.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Идентификатор вещи, которую хотят забронировать.
     */
    @JsonProperty("itemId")
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    /**
     * Вещь, которая бронируется.
     */
    @JsonProperty("item")
    private ItemDto item;

    /**
     * Статус бронирования.
     */
    @JsonProperty("status")
    private BookingStatus status;

    /**
     * Пользователь, который осуществляет бронирование.
     */
    @JsonProperty("booker")
    private UserDto booker;

    /**
     * Идентификатор пользователя, который бронирует.
     */
    @JsonProperty("bookerId")
    private Long bookerId;

}
