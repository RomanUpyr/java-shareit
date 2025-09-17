package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория бронирований.
 */
@Repository
public class InMemoryBookingRepository implements BookingRepository {
    // Хранилище вещей: id -> Booking>
    private final Map<Long, Booking> bookings = new HashMap<>();
    // Счетчик для генерации уникальных идентификаторов
    private long nextId = 1L;

    /**
     * Сохраняет новое бронирование.
     */
    @Override
    public Booking save(Booking booking) {
        booking.setId(nextId++);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    /**
     * Находит бронирование по идентификатору.
     */
    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    /**
     * Возвращает все бронирования.
     */
    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    /**
     * Находит все бронирования определенного пользователя.
     */
    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит все бронирования вещей определенного владельца.
     */
    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит все бронирования с определенным статусом для пользователя.
     */
    @Override
    public List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .filter(booking -> booking.getStatus() == status)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит все бронирования с определенным статусом для владельца.
     */
    @Override
    public List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> booking.getStatus() == status)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит текущие бронирования пользователя.
     */
    @Override
    public List<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит текущие бронирования владельца.
     */
    @Override
    public List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит будущие бронирования пользователя.
     */
    @Override
    public List<Booking> findFutureByBookerId(Long bookerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .filter(booking -> booking.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит будущие бронирования владельца.
     */
    @Override
    public List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> booking.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит прошедшие бронирования пользователя.
     */
    @Override
    public List<Booking> findPastByBookerId(Long bookerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .filter(booking -> booking.getEnd().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит прошедшие бронирования владельца.
     */
    @Override
    public List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> booking.getEnd().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные бронирования.
     */
    @Override
    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    /**
     * Удаляет бронирование по идентификатору.
     */
    @Override
    public void deleteById(Long id) {
        bookings.remove(id);
    }

    /**
     * Проверяет, доступна ли вещь для бронирования в указанный период.
     */
    @Override
    public boolean isItemAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        return bookings.values().stream()
                .filter(booking -> !booking.getId().equals(excludeBookingId))     // Исключаем текущее бронирование при обновлении
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED) // Только подтвержденные бронирования
                .noneMatch(booking -> isTimeOverlap(booking.getStart(), booking.getEnd(), start, end));
    }

    /**
     * Проверяет пересечение двух временных интервалов
     */
    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
