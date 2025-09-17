package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с бронированиями.
 */
public interface BookingRepository {
    /**
     * Сохраняет новое бронирование.
     */
    Booking save(Booking booking);

    /**
     * Находит бронирование по идентификатору.
     */
    Optional<Booking> findById(Long id);

    /**
     * Возвращает все бронирования.
     */
    List<Booking> findAll();

    /**
     * Находит все бронирования определенного пользователя.
     */
    List<Booking> findByBookerId(Long bookerId);

    /**
     * Находит все бронирования вещей определенного владельца.
     */
    List<Booking> findByOwnerId(Long ownerId);

    /**
     * Находит все бронирования с определенным статусом для пользователя.
     */
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    /**
     * Находит все бронирования с определенным статусом для владельца.
     */
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status);

    /**
     * Находит текущие бронирования пользователя.
     */
    List<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now);

    /**
     * Находит текущие бронирования владельца.
     */
    List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now);

    /**
     * Находит будущие бронирования пользователя.
     */
    List<Booking> findFutureByBookerId(Long bookerId, LocalDateTime now);

    /**
     * Находит будущие бронирования владельца.
     */
    List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime now);

    /**
     * Находит прошедшие бронирования пользователя.
     */
    List<Booking> findPastByBookerId(Long bookerId, LocalDateTime now);

    /**
     * Находит прошедшие бронирования владельца.
     */
    List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime now);

    /**
     * Обновляет данные бронирования.
     */
    Booking update(Booking booking);

    /**
     * Удаляет бронирование по идентификатору.
     */
    void deleteById(Long id);

    /**
     * Проверяет, доступна ли вещь для бронирования в указанный период.
     */
    boolean isItemAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end, Long excludeBookingId);
}
