package ru.practicum.shareit.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия для получения прошедших бронирований владельца.
 */
@Component
@RequiredArgsConstructor
public class PastOwnerStrategy implements BookingStateFetchStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findPastByOwnerId(userId, LocalDateTime.now());
    }
}
