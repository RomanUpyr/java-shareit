package ru.practicum.shareit.gt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gt.booking.dto.BookingDto;
import ru.practicum.shareit.client.BookingClient;

/**
 * REST контроллер для работы с бронированиями.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;


    // Имя заголовка для передачи идентификатора пользователя
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новое бронирование с валидацией группы Create
     */
    @PostMapping
    public ResponseEntity<BookingDto> create(@Validated(BookingDto.Create.class) @RequestBody BookingDto bookingDto,
                                         @RequestHeader(USER_ID_HEADER) Long bookerId) {
        return bookingClient.create(bookingDto, bookerId);
    }

    /**
     * Подтверждает или отклоняет бронирование
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@PathVariable Long bookingId,
                                          @RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(bookingId, ownerId, approved);
    }

    /**
     * Возвращает информацию о бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    /**
     * Возвращает бронирования пользователя с фильтрацией по состоянию
     */
    @GetMapping
    public ResponseEntity<BookingDto> getByBookerId(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByBookerId(bookerId, state);
    }

    /**
     * Возвращает бронирования вещей владельца с фильтрацией по состоянию
     */
    @GetMapping("/owner")
    public ResponseEntity<BookingDto> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByOwnerId(ownerId, state);
    }

    /**
     * Обновляет бронирование
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(@PathVariable Long bookingId,
                                         @Validated(BookingDto.Update.class) @RequestBody BookingDto bookingDto,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.update(bookingId, bookingDto, userId);
    }

    /**
     * Отменяет бронирование
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingDto> cancel(@PathVariable Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.cancel(bookingId, userId);
    }
}
