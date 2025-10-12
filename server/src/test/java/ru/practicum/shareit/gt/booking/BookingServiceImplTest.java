package ru.practicum.shareit.gt.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.gt.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingStrategyContext strategyContext;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_ShouldCreateBooking_WhenValidData() {
        Long bookerId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        User booker = new User(bookerId, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(bookingDto, bookerId)).thenReturn(booking);
        when(bookingRepository.existOverlappingBookings(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.create(bookingDto, bookerId);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void create_ShouldThrowException_WhenOwnerBooksOwnItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        User owner = new User(ownerId, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .item(item)
                .booker(owner)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(bookingDto, ownerId)).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, ownerId));
    }

    @Test
    void create_ShouldThrowException_WhenItemNotAvailable() {
        Long bookerId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        User booker = new User(bookerId, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(false)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(bookingDto, bookerId)).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, bookerId));
    }

    @Test
    void approve_ShouldApproveBooking_WhenUserIsOwner() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        boolean approved = true;

        User owner = new User(ownerId, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.approve(bookingId, ownerId, approved);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_ShouldThrowException_WhenUserNotOwner() {
        Long bookingId = 1L;
        Long notOwnerId = 3L;

        User owner = new User(2L, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class,
                () -> bookingService.approve(bookingId, notOwnerId, true));
    }

    @Test
    void getById_ShouldReturnBooking_WhenUserIsBooker() {
        Long bookingId = 1L;
        Long userId = 1L;

        User booker = new User(userId, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> bookingService.getById(bookingId, userId));
    }

    @Test
    void getById_ShouldThrowException_WhenUserHasNoAccess() {
        Long bookingId = 1L;
        Long userId = 3L;

        User booker = new User(1L, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
    }
}