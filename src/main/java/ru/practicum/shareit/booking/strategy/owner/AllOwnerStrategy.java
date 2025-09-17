package ru.practicum.shareit.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;

import java.util.List;

/**
 * Стратегия для получения всех бронирований вещей владельца.
 */
@Component
@RequiredArgsConstructor
public class AllOwnerStrategy implements BookingStateFetchStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByOwnerId(userId);
    }

    @Override
    public String getState() {
        return "ALL";
    }
}
