package ru.practicum.shareit.booking.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.strategy.*;
import ru.practicum.shareit.booking.strategy.booker.*;
import ru.practicum.shareit.booking.strategy.owner.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурационный класс для настройки паттерна "Стратегия".
 */
@Configuration
public interface BookingStrategyConfig {
    @Bean
    default   Map<String, BookingStateFetchStrategy> bookerStrategies(
            AllBookerStrategy allBookerStrategy,
            CurrentBookerStrategy currentBookerStrategy,
            FutureBookerStrategy futureBookerStrategy,
            PastBookerStrategy pastBookerStrategy,
            WaitingBookerStrategy waitingBookerStrategy,
            RejectedBookerStrategy rejectedBookerStrategy,
            ApprovedBookerStrategy approvedBookerStrategy) {

        Map<String, BookingStateFetchStrategy> strategies = new HashMap<>();
        strategies.put("ALL", allBookerStrategy);
        strategies.put("CURRENT", currentBookerStrategy);
        strategies.put("FUTURE", futureBookerStrategy);
        strategies.put("PAST", pastBookerStrategy);
        strategies.put("WAITING", waitingBookerStrategy);
        strategies.put("REJECTED", rejectedBookerStrategy);
        strategies.put("APPROVED", approvedBookerStrategy);

        return strategies;
    }


    @Bean
    default Map<String, BookingStateFetchStrategy> ownerStrategies(
            AllOwnerStrategy allOwnerStrategy,
            CurrentOwnerStrategy currentOwnerStrategy,
            FutureOwnerStrategy futureOwnerStrategy,
            PastOwnerStrategy pastOwnerStrategy,
            WaitingOwnerStrategy waitingOwnerStrategy,
            RejectedOwnerStrategy rejectedOwnerStrategy,
            ApprovedOwnerStrategy approvedOwnerStrategy) {

        Map<String, BookingStateFetchStrategy> strategies = new HashMap<>();
        strategies.put("ALL", allOwnerStrategy);
        strategies.put("CURRENT", currentOwnerStrategy);
        strategies.put("FUTURE", futureOwnerStrategy);
        strategies.put("PAST", pastOwnerStrategy);
        strategies.put("WAITING", waitingOwnerStrategy);
        strategies.put("REJECTED", rejectedOwnerStrategy);
        strategies.put("APPROVED", approvedOwnerStrategy);

        return strategies;
    }
}
