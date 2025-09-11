package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

/**
 * Маппер для преобразования между Entity и DTO объектов бронирования.
 */
@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    /**
     * Преобразует DTO бронирования в Entity для сохранения в базу данных.
     */
    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDto.getItemId()));

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus() != null ? bookingDto.getStatus() : BookingStatus.WAITING);

        return booking;
    }

    /**
     * Преобразует Entity бронирования в DTO для возврата клиенту.
     */
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                itemMapper.toItemDto(booking.getItem()),
                userMapper.toUserDto(booking.getBooker()),
                booking.getBooker().getId()
        );
    }

    /**
     * Преобразует Entity бронирования в краткий DTO.
     */
    public BookingDto toBookingDtoShort(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBookerId(booking.getBooker().getId());
        dto.setItemId(booking.getItem().getId());
        return dto;
    }

    /**
     * Преобразует Entity бронирования в DTO для ответа на запрос.
     * Автоматически определяет уровень детализации на основе контекста.
     */
    public BookingDto toBookingDto(Booking booking, boolean includeDetails) {
        if (includeDetails) {
            return toBookingDto(booking);
        } else {
            return toBookingDtoShort(booking);
        }
    }

}
