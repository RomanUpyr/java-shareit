package ru.practicum.shareit.gt.booking.strategy.booker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.dto.BookingStatus;
import ru.practicum.shareit.gt.booking.model.Booking;

import java.util.List;


/**
 * Стратегия для получения "ПОДТВЕРЖДЕННЫХ" бронирований пользователя.
 */
@Component
@RequiredArgsConstructor
public class ApprovedBookerStrategy implements BookerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
    }

    @Override
    public String getState() {
        return "APPROVED";
    }
}
