package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * DTO для работы с бронированиями.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    /**
     * Уникальный идентификатор бронирования.
     */
    private Long id;

    /**
     * Идентификатор вещи, которую хотят забронировать.
     */
    @NotNull(message = "Item ID не должен быть null", groups = {Create.class})
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     */
    @NotNull(message = "Дата начала бронирования не может быть null", groups = {Create.class, Update.class})
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или настоящем", groups = {Create.class, Update.class})
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @NotNull(message = "Дата окончания бронирования не может быть null", groups = {Create.class, Update.class})
    @Future(message = "Дата окончания бронирования должна быть в будущем", groups = {Create.class, Update.class})
    private LocalDateTime end;

    /**
     * Вещь, которая бронируется.
     */
    private ItemDto item;

    /**
     * Статус бронирования.
     */
    private BookingStatus status;

    /**
     * Пользователь, который осуществляет бронирование.
     */
    private UserDto booker;

    /**
     * Идентификатор пользователя, который бронирует.
     */
    private Long bookerId;

    /**
     * Маркерный интерфейс для групповой валидации при создании.
     */
    public interface Create {
    }

    /**
     * Маркерный интерфейс для групповой валидации при обновлении.
     */
    public interface Update {
    }

    /**
     * Конструктор для создания DTO из entity с полной информацией.
     */
    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                      ItemDto item, UserDto booker, Long bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.item = item;
        this.booker = booker;
        this.bookerId = bookerId;
        this.itemId = (item != null) ? item.getId() : null;
    }

    /**
     * Конструктор для создания DTO из entity с краткой информацией.
     */
    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status, Long bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.bookerId = bookerId;
    }

    /**
     * Проверяет, является ли DTO кратким (без вложенных объектов).
     */
    public boolean isShort() {
        return item == null && booker == null;
    }
}
