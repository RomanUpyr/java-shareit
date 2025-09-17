package ru.practicum.shareit.booking.strategy.booker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;

import java.util.List;

/**
 * Стратегия для получения бронирований пользователя в статусе "ОЖИДАНИЕ".
 */
@Component
@RequiredArgsConstructor
public class WaitingBookerStrategy implements BookingStateFetchStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
    }

    @Override
    public String getState() {
        return "WAITING";
    }
}
