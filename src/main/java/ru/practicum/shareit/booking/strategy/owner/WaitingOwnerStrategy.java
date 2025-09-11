package ru.practicum.shareit.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;

import java.util.List;

/**
 * Стратегия для получения бронирований владельца в статусе "ОЖИДАНИЕ".
 */
@Component
@RequiredArgsConstructor
public class WaitingOwnerStrategy implements BookingStateFetchStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING);
    }
}
