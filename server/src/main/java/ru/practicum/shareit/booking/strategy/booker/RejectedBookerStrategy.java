package ru.practicum.shareit.server.booking.strategy.booker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;

import java.util.List;

/**
 * Стратегия для получения "ОТКЛОНЕННЫХ" бронирований пользователя.
 */
@Component
@RequiredArgsConstructor
public class RejectedBookerStrategy implements BookerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    @Override
    public String getState() {
        return "REJECTED";
    }
}
