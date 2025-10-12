package ru.practicum.shareit.gt.booking.strategy.booker;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.model.Booking;

import java.util.List;

/**
 * Стратегия для получения всех бронирований пользователя.
 */
@Component
@RequiredArgsConstructor
public class AllBookerStrategy implements BookerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdOrderByStartDesc(userId);
    }

    @Override
    public String getState() {
        return "ALL";
    }


}
