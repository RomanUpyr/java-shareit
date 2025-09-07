package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса бронирований.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * Создает бронирование с проверками:
     */
    @Override
    public BookingDto create(BookingDto bookingDto, Long bookerId) {
        // Проверяем существование пользователя
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        Booking booking = BookingMapper.toBooking(bookingDto, bookerId, itemRepository, userRepository);

        validateBooking(booking, bookerId);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    /**
     * Подтверждает или отклоняет бронирование владельцем вещи
     */
    @Override
    public BookingDto approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Проверяем что пользователь является владельцем вещи
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        // Проверяем что бронирование еще не обработано
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }

        // Устанавливаем новый статус
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.update(booking));
    }

    /**
     * Находит бронирование с проверкой прав доступа
     */
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Проверяем права доступа: автор бронирования или владелец вещи
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied to booking");
        }

        return BookingMapper.toBookingDto(booking);
    }

    /**
     * Возвращает бронирования пользователя с фильтрацией по состоянию
     */
    @Override
    public List<BookingDto> getByBookerId(Long bookerId, String state) {
        // Проверяем существование пользователя
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = getBookingsByState(bookerId, state, now, true);

        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking, true))
            .collect(Collectors.toList());
    }

    /**
     * Возвращает бронирования вещей владельца с фильтрацией по состоянию
     */
    @Override
    public List<BookingDto> getByOwnerId(Long ownerId, String state) {
        // Проверяем существование пользователя
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = getBookingsByState(ownerId, state, now, false);

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
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

        return BookingMapper.toBookingDto(bookingRepository.update(existingBooking));
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
        return BookingMapper.toBookingDto(bookingRepository.update(booking));
    }
    /**
     * Удаляет бронирование.
     */
    @Override
    public void delete(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private List<Booking> getBookingsByState(Long userId, String state, LocalDateTime now, boolean isBooker) {
        switch (state.toUpperCase()) {
            case "ALL":
                return isBooker ? bookingRepository.findByBookerId(userId) : bookingRepository.findByOwnerId(userId);
            case "CURRENT":
                return isBooker ? bookingRepository.findCurrentByBookerId(userId, now) :
                        bookingRepository.findCurrentByOwnerId(userId, now);
            case "FUTURE":
                return isBooker ? bookingRepository.findFutureByBookerId(userId, now) :
                        bookingRepository.findFutureByOwnerId(userId, now);
            case "PAST":
                return isBooker ? bookingRepository.findPastByBookerId(userId, now) :
                        bookingRepository.findPastByOwnerId(userId, now);
            case "WAITING":
                return isBooker ? bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING) :
                        bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case "REJECTED":
                return isBooker ? bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED) :
                        bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    /**
     * Валидация бизнес-правил бронирования
     */
    private void validateBooking(Booking booking, Long bookerId) {
        Item item = booking.getItem();

        // Владелец не может бронировать свою вещь
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        // Вещь должна быть доступна для аренды
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Item is not available for booking");
        }

        // Дата начала должна быть раньше даты окончания
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start date must be before end date");
        }

        // Дата начала не должна быть в прошлом
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in past");
        }

        // Проверяем доступность вещи в указанный период
        if (!bookingRepository.isItemAvailableForBooking(
                item.getId(), booking.getStart(), booking.getEnd(), booking.getId())) {
            throw new ValidationException("Item is already booked for this period");
        }
    }
}
