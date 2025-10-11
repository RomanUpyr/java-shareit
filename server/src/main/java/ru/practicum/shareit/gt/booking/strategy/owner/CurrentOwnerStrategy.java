package ru.practicum.shareit.gt.booking.strategy.owner;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.model.Booking;

import java.util.List;

import java.time.LocalDateTime;

/**
 * Стратегия для получения "ТЕКУЩИХ" бронирований вещей владельца.
 */
@Component
@RequiredArgsConstructor
public class CurrentOwnerStrategy implements OwnerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByItemOwnerIdAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public String getState() {
        return "CURRENT";
    }
}
