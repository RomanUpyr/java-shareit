package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

/**
 * Маппер для преобразования между Entity и DTO объектов бронирования.
 */
@Component
@RequiredArgsConstructor
@Builder
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

        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDto.getStatus() != null ? bookingDto.getStatus() : BookingStatus.WAITING)
                .build();
    }

    /**
     * Преобразует Entity бронирования в DTO для возврата клиенту.
     */
    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        ItemDto itemDto = itemMapper.toItemDto(booking.getItem());
        UserDto bookerDto = userMapper.toUserDto(booking.getBooker());

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .item(itemDto)
                .booker(bookerDto)
                .build();
    }

    /**
     * Преобразует Entity бронирования в краткий DTO.
     */
    public BookingDto toBookingDtoShort(Booking booking) {

        if (booking == null) {
            return null;
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
    }

    /**
     * Преобразует Entity бронирования в DTO для ответа на запрос.
     * Автоматически определяет уровень детализации на основе контекста.
     */
    public BookingDto toBookingDto(Booking booking, boolean includeDetails) {
        if (booking == null) {
            return null;
        }

        if (includeDetails) {
            return toBookingDto(booking);
        } else {
            return toBookingDtoShort(booking);
        }
    }

}
