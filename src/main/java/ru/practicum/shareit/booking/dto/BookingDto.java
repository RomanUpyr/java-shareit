package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private Long id;

    /**
     * Идентификатор вещи, которую хотят забронировать.
     */
    @JsonProperty("itemId")
    @NotNull(message = "Item ID не должен быть null", groups = {Create.class})
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Дата начала бронирования не может быть null", groups = {Create.class, Update.class})
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или настоящем", groups = {Create.class, Update.class})
    @JsonProperty("start")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Дата окончания бронирования не может быть null", groups = {Create.class, Update.class})
    @Future(message = "Дата окончания бронирования должна быть в будущем", groups = {Create.class, Update.class})
    @JsonProperty("end")
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
