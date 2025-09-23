package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контекст для управления стратегиями получения бронирований.
 */
@Component
public class BookingStrategyContext {
    private final Map<String, BookingStateFetchStrategy> bookerStrategyMap = new HashMap<>();
    private final Map<String, BookingStateFetchStrategy> ownerStrategyMap = new HashMap<>();

    public BookingStrategyContext(List<BookingStateFetchStrategy> strategies) {
        for (BookingStateFetchStrategy strategy : strategies) {
            if (strategy.getClass().getSimpleName().toLowerCase().contains("owner")) {
                ownerStrategyMap.put(strategy.getState().toUpperCase(), strategy);
            } else {
                bookerStrategyMap.put(strategy.getState().toUpperCase(), strategy);
            }
        }
    }

    public List<Booking> executeBookerStrategy(String state, Long userId, BookingRepository bookingRepository) {
        BookingStateFetchStrategy strategy = bookerStrategyMap.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown booking state for booker: " + state);
        }
        return strategy.findBookings(userId, bookingRepository);
    }

    public List<Booking> executeOwnerStrategy(String state, Long userId, BookingRepository bookingRepository) {
        BookingStateFetchStrategy strategy = ownerStrategyMap.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown booking state for owner: " + state);
        }
        return strategy.findBookings(userId, bookingRepository);
    }
}
