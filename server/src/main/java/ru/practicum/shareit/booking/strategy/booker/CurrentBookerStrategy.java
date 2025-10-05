package ru.practicum.shareit.server.booking.strategy.booker;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.model.Booking;

import java.util.List;

import java.time.LocalDateTime;

/**
 * Стратегия для получения текущих бронирований пользователя.
 */
@Component
@RequiredArgsConstructor
public class CurrentBookerStrategy implements BookerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public String getState() {
        return "CURRENT";
    }
}
