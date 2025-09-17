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
    private final Map<String, BookingStateFetchStrategy> strategyMap = new HashMap<>();

    public BookingStrategyContext(List<BookingStateFetchStrategy> strategies) {
        for (BookingStateFetchStrategy strategy : strategies) {
            strategyMap.put(strategy.getState().toUpperCase(), strategy);
        }
    }

    public BookingStateFetchStrategy getStrategy(String state) {
        BookingStateFetchStrategy strategy = strategyMap.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Не известное состояние бронирования: " + state);
        }
        return strategy;
    }

    public List<Booking> executeStrategy(String state, Long userId, BookingRepository bookingRepository) {
        BookingStateFetchStrategy strategy = getStrategy(state);
        return strategy.findBookings(userId, bookingRepository);
    }
}
