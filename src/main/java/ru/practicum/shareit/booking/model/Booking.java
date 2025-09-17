package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель бронирования вещи.
 * Представляет запрос на аренду вещи на определенный период времени.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    /**
     * Уникальный идентификатор бронирования.
     */
    @NotNull(message = "Booking Id не должен быть null")
    private Long id;

    /**
     * Дата и время начала бронирования.
     */
    @NotNull(message = "Дата начала бронирования не может быть null")
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или настоящем")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @NotNull(message = "Дата окончания бронирования не может быть null")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;

    /**
     * Вещь, которая бронируется.
     */
    @NotNull(message = "Booking item не может быть null")
    private Item item;

    /**
     * Пользователь, который осуществляет бронирование
     */
    private User booker;

    /**
     * Статус бронирования.
     * Определяет текущее состояние запроса на бронирование.
     */
    @NotNull(message = "Booking status не может быть null")
    private BookingStatus status;

}
