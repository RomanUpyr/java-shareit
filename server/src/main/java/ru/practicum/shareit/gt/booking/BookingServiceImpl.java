package ru.practicum.shareit.gt.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.gt.booking.dto.BookingDto;
import ru.practicum.shareit.gt.booking.dto.BookingStatus;
import ru.practicum.shareit.gt.booking.model.Booking;
import ru.practicum.shareit.gt.booking.strategy.BookingStrategyContext;
import ru.practicum.shareit.gt.exception.AccessDeniedException;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.exception.ValidationException;
import ru.practicum.shareit.gt.item.ItemRepository;
import ru.practicum.shareit.gt.item.model.Item;
import ru.practicum.shareit.gt.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса бронирований.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final BookingStrategyContext strategyContext;

    /**
     * Создает бронирование с проверками:
     */
    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Long bookerId) {
        try {
            // Проверяем существование пользователя
            userRepository.findById(bookerId)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

            Item item = itemRepository.findById(bookingDto.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDto.getItemId()));
        } catch (Exception e) {
            log.error("Error in create booking", e);            throw e;
        }

        Booking booking = bookingMapper.toBooking(bookingDto, bookerId);

        validateBooking(booking, bookerId);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    /**
     * Подтверждает или отклоняет бронирование владельцем вещи
     */
    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = getBookingById(bookingId);

        // Проверяем что пользователь является владельцем вещи
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("User is not the owner of the item");
        }

        // Проверяем что бронирование еще не обработано
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }

        // Устанавливаем новый статус
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    /**
     * Находит бронирование с проверкой прав доступа
     */
    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        // Проверяем права доступа: автор бронирования или владелец вещи
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied to booking");
        }

        return bookingMapper.toBookingDto(booking);
    }

    /**
     * Получает бронирование по ID с проверкой существования
     */
    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    /**
     * Возвращает бронирования пользователя с фильтрацией по состоянию
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByBookerId(Long bookerId, String state) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User not found with id: " + bookerId);
        }

        // Используем контекст стратегий для получения бронирований
        List<Booking> bookings = strategyContext.executeBookerStrategy(state, bookerId, bookingRepository);

        return bookings.stream()
                .map(booking -> bookingMapper.toBookingDto(booking, true))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает бронирования вещей владельца с фильтрацией по состоянию
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByOwnerId(Long ownerId, String state) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }
        // Используем контекст стратегий для получения бронирований
        List<Booking> bookings = strategyContext.executeOwnerStrategy(state, ownerId, bookingRepository);

        return bookings.stream()
                .map(booking -> bookingMapper.toBookingDto(booking, true))
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные бронирования.
     */
    @Override
    public BookingDto update(Long bookingId, BookingDto bookingDto, Long userId) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Проверяем что пользователь является автором бронирования
        if (!existingBooking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("User is not the booker of this booking");
        }

        // Проверяем что бронирование еще не началось
        if (existingBooking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot update started booking");
        }

        // Обновляем данные
        if (bookingDto.getStart() != null) {
            existingBooking.setStart(bookingDto.getStart());
        }
        if (bookingDto.getEnd() != null) {
            existingBooking.setEnd(bookingDto.getEnd());
        }
        if (bookingDto.getItemId() != null &&
                !bookingDto.getItemId().equals(existingBooking.getItem().getId())) {
            Item newItem = itemRepository.findById(bookingDto.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDto.getItemId()));
            existingBooking.setItem(newItem);
        }

        validateBooking(existingBooking, userId);

        return bookingMapper.toBookingDto(bookingRepository.save(existingBooking));
    }

    /**
     * Отменяет бронирование.
     */
    @Override
    public BookingDto cancel(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Проверяем что пользователь является автором бронирования
        if (!booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("User is not the booker of this booking");
        }

        // Проверяем что бронирование еще не началось
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot cancel started booking");
        }

        // Проверяем что бронирование еще не обработано владельцем
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Cannot cancel processed booking");
        }

        booking.setStatus(BookingStatus.CANCELED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    /**
     * Удаляет бронирование.
     */
    @Override
    public void delete(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }


    /**
     * Валидация бизнес-правил бронирования
     */
    private void validateBooking(Booking booking, Long bookerId) {
        Item item = booking.getItem();
        LocalDateTime now = LocalDateTime.now();

        // Владелец не может бронировать свою вещь
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Owner cannot book own item");
        }

        // Вещь должна быть доступна для аренды
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Item is not available for booking");
        }

        // Дата начала должна быть раньше даты окончания
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start date must be before end date");
        }

        // Дата начала не должна быть в будущем
        if (booking.getEnd().isBefore(now)) {
            throw new ValidationException("End date must be in future");
        }

        // Проверяем доступность вещи в указанный период
        boolean hasOverlap = bookingRepository.existOverlappingBookings(
                item.getId(), booking.getStart(), booking.getEnd(), booking.getId());

        if (hasOverlap) {
            throw new ValidationException("Item is already booked for this period");
        }
    }
}
