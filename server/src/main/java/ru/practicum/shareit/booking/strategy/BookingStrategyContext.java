package ru.practicum.shareit.server.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.strategy.booker.BookerStrategy;
import ru.practicum.shareit.server.booking.strategy.owner.OwnerStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Контекст для управления стратегиями получения бронирований.
 */
@Component
public class BookingStrategyContext {
    private final Map<String, BookerStrategy> bookerStrategies;
    private final Map<String, OwnerStrategy> ownerStrategies;

    public BookingStrategyContext(
            List<BookerStrategy> bookerStrategyList,
            List<OwnerStrategy> ownerStrategyList) {
        this.bookerStrategies = bookerStrategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getState().toUpperCase(),
                        Function.identity()
                ));
        this.ownerStrategies = ownerStrategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getState().toUpperCase(),
                        Function.identity()
                ));
    }

    public List<Booking> executeBookerStrategy(String state, Long userId, BookingRepository bookingRepository) {
        BookingStateFetchStrategy strategy = bookerStrategies.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown booking state for booker: " + state);
        }
        return strategy.findBookings(userId, bookingRepository);
    }

    public List<Booking> executeOwnerStrategy(String state, Long userId, BookingRepository bookingRepository) {
        BookingStateFetchStrategy strategy = ownerStrategies.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown booking state for owner: " + state);
        }
        return strategy.findBookings(userId, bookingRepository);
    }
}
