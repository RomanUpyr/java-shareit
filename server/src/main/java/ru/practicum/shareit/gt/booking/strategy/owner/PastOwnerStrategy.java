package ru.practicum.shareit.gt.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия для получения прошедших бронирований владельца.
 */
@Component
@RequiredArgsConstructor
public class PastOwnerStrategy implements OwnerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public String getState() {
        return "PAST";
    }
}
