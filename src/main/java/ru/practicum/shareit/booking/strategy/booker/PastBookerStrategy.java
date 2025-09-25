package ru.practicum.shareit.booking.strategy.booker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия для получения прошедших бронирований пользователя.
 */
@Component
@RequiredArgsConstructor
public class PastBookerStrategy implements BookerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public String getState() {
        return "PAST";
    }
}
