package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с бронированиями.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования определенного пользователя, отсортированные по дате начала.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId);
    /**
     * Находит бронирования пользователя с определенным статусом, отсортированные по дате начала.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(@Param("bookerId") Long bookerId, @Param("status") BookingStatus status);

    /**
     * Находит текущие бронирования пользователя.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.start <= :start AND b.end >= :start ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStartBeforeOrderByStartDesc(
            @Param("bookerId") Long bookerId, @Param("start") LocalDateTime start);

    /**
     * Находит завершенные бронирования пользователя.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.end <= :end ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(@Param("bookerId") Long bookerId, @Param("end") LocalDateTime end);

    /**
     * Находит будущие бронирования пользователя.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.start >= :start ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(@Param("bookerId") Long bookerId, @Param("start") LocalDateTime start);

    /**
     * Находит все бронирования вещей владельца, отсортированные по дате начала.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    /**
     * Находит бронирования вещей владельца с определенным статусом.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    /**
     * Находит текущие бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.start <= :start AND b.end >= :start ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartBeforeOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("start") LocalDateTime start);

    /**
     * Находит завершенные бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.end <= :end ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("end") LocalDateTime end);

    /**
     * Находит будущие бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.start >= :start ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("start") LocalDateTime start);

    /**
     * Находит пересекающиеся бронирования для указанной вещи.
     * Используется для проверки доступности вещи в заданный период.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId) " +
            "AND ((b.start BETWEEN :start AND :end) OR (b.end BETWEEN :start AND :end) " +
            "OR (b.start <= :start AND b.end >= :end))")
    List<Booking> findOverlappingBookings(@Param("itemId") Long itemId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("excludeBookingId") Long excludeBookingId);

    /**
     * Находит последнее завершенное бронирование вещи пользователем.
     * Используется для проверки возможности оставить отзыв.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.status = :status AND b.end <= :end ORDER BY b.end DESC")
    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
            @Param("itemId") Long itemId, @Param("bookerId") Long bookerId, @Param("status") BookingStatus status, @Param("end") LocalDateTime end);

}
