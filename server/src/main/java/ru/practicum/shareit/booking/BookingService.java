package ru.practicum.shareit.server.booking;

import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.exception.NotFoundException;

import java.util.List;

/**
 * Интерфейс сервиса для работы с бронированиями.
 */
public interface BookingService {
    /**
     * Создает новое бронирование.
     *
     * @param bookingDto DTO с данными для создания бронирования.
     * @param bookerId   идентификатор пользователя, который бронирует.
     * @return созданное бронирование в формате DTO.
     * @throws NotFoundException   если вещь или пользователь не найдены.
     * @throws ValidationException если вещь недоступна или даты некорректны.
     */
    BookingDto create(BookingDto bookingDto, Long bookerId);

    /**
     * Подтверждает или отклоняет бронирование владельцем вещи.
     *
     * @param bookingId идентификатор бронирования.
     * @param ownerId   идентификатор владельца вещи.
     * @param approved  true - подтвердить, false - отклонить.
     * @return обновленное бронирование в формате DTO.
     * @throws NotFoundException если бронирование не найдено или пользователь не владелец.
     */
    BookingDto approve(Long bookingId, Long ownerId, boolean approved);

    /**
     * Находит бронирование по идентификатору.
     *
     * @param bookingId идентификатор бронирования.
     * @param userId    идентификатор пользователя, запрашивающего информацию.
     * @return бронирование в формате DTO.
     * @throws NotFoundException если бронирование не найдено или нет прав доступа.
     */
    BookingDto getById(Long bookingId, Long userId);

    /**
     * Возвращает все бронирования пользователя с фильтрацией по статусу.
     *
     * @param bookerId идентификатор пользователя.
     * @param state    состояние бронирования.
     * @return список бронирований в формате DTO.
     */
    List<BookingDto> getByBookerId(Long bookerId, String state);

    /**
     * Возвращает все бронирования вещей владельца с фильтрацией по статусу.
     *
     * @param ownerId идентификатор владельца вещей.
     * @param state   состояние бронирования.
     * @return список бронирований в формате DTO.
     */
    List<BookingDto> getByOwnerId(Long ownerId, String state);

    /**
     * Обновляет данные бронирования.
     */
    BookingDto update(Long bookingId, BookingDto bookingDto, Long userId);

    /**
     * Отменяет бронирование.
     */
    BookingDto cancel(Long bookingId, Long userId);

    /**
     * Удаляет бронирование.
     */
    void delete(Long bookingId);
}
