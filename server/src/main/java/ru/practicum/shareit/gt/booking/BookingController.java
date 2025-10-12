package ru.practicum.shareit.gt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gt.booking.dto.BookingDto;

import java.util.List;

/**
 * REST контроллер для работы с бронированиями.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;


    // Имя заголовка для передачи идентификатора пользователя
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новое бронирование с валидацией группы Create
     */
    @PostMapping
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader(USER_ID_HEADER) Long bookerId) {
        return bookingService.create(bookingDto, bookerId);
    }

    /**
     * Подтверждает или отклоняет бронирование
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestHeader(USER_ID_HEADER) Long ownerId,
                              @RequestParam boolean approved) {
        return bookingService.approve(bookingId, ownerId, approved);
    }

    /**
     * Возвращает информацию о бронировании
     */
    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    /**
     * Возвращает бронирования пользователя с фильтрацией по состоянию
     */
    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByBookerId(bookerId, state);
    }

    /**
     * Возвращает бронирования вещей владельца с фильтрацией по состоянию
     */
    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByOwnerId(ownerId, state);
    }

    /**
     * Обновляет бронирование
     */
    @PutMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestBody BookingDto bookingDto,
                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.update(bookingId, bookingDto, userId);
    }

    /**
     * Отменяет бронирование
     */
    @DeleteMapping("/{bookingId}")
    public BookingDto cancel(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.cancel(bookingId, userId);
    }
}
