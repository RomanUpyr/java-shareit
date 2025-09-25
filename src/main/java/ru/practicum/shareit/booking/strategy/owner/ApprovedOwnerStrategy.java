package ru.practicum.shareit.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

/**
 * Стратегия для получения "ПОДТВЕРЖДЕННЫХ" бронирований владельца.
 */
@Component
@RequiredArgsConstructor
public class ApprovedOwnerStrategy implements OwnerStrategy {
    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository) {
        return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
    }

    @Override
    public String getState() {
        return "APPROVED";
    }
}
