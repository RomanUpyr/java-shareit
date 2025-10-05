package ru.practicum.shareit.server.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.model.Booking;

import java.util.List;

/**
 * Стратегия для получения всех бронирований вещей владельца.
 */
@Component
@RequiredArgsConstructor
public class AllOwnerStrategy implements OwnerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
    }

    @Override
    public String getState() {
        return "ALL";
    }
}
