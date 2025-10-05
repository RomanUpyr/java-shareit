package ru.practicum.shareit.server.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;

import java.util.List;

/**
 * Стратегия для получения "ОТКЛОНЕННЫХ" бронирований владельца.
 */
@Component
@RequiredArgsConstructor
public class RejectedOwnerStrategy implements OwnerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    @Override
    public String getState() {
        return "REJECTED";
    }
}
