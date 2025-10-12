package ru.practicum.shareit.gt.booking.strategy;

import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.model.Booking;

import java.util.List;

/**
 * Интерфейс стратегии для получения бронирований по различным состояниям.
 */
public interface BookingStateFetchStrategy {
    /**
     * Находит бронирования согласно конкретной стратегии.
     *
     * @param userId            идентификатор пользователя (booker или owner)
     * @param bookingRepository репозиторий для доступа к данным бронирований
     * @return список бронирований, отфильтрованных по стратегии
     */
    List<Booking> findBookings(Long userId, BookingRepository bookingRepository);

    /**
     * Возвращает тип состояния для маппинга стратегии.
     */
    String getState();
}
