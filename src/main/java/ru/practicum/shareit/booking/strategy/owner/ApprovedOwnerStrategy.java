package ru.practicum.shareit.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия для получения "ПОДТВЕРЖДЕННЫХ" бронирований владельца.
 */
@Component
@RequiredArgsConstructor
public class ApprovedOwnerStrategy implements BookingStateFetchStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.APPROVED);
    }
}
