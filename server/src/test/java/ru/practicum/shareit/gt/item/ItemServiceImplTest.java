package ru.practicum.shareit.gt.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.gt.booking.BookingRepository;
import ru.practicum.shareit.gt.booking.dto.BookingStatus;
import ru.practicum.shareit.gt.booking.model.Booking;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.exception.ValidationException;
import ru.practicum.shareit.gt.item.dto.CommentDto;
import ru.practicum.shareit.gt.item.dto.ItemDto;
import ru.practicum.shareit.gt.item.model.Comment;
import ru.practicum.shareit.gt.item.model.Item;
import ru.practicum.shareit.gt.user.UserRepository;
import ru.practicum.shareit.gt.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_ShouldCreateItem_WhenValidData() {
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        User owner = new User(ownerId, "Owner", "owner@email.com");
        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(itemDto, ownerId)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.create(itemDto, ownerId);

        assertNotNull(result);
        verify(itemRepository).save(item);
    }

    @Test
    void create_ShouldThrowException_WhenOwnerNotFound() {
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, ownerId));
    }

    @Test
    void update_ShouldUpdateItem_WhenUserIsOwner() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        User owner = new User(ownerId, "Owner", "owner@email.com");
        Item existingItem = Item.builder()
                .id(itemId)
                .name("Original Name")
                .description("Original Description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(existingItem);
        when(itemMapper.toItemDto(existingItem)).thenReturn(updateDto);

        ItemDto result = itemService.update(itemId, updateDto, ownerId);

        assertNotNull(result);
        assertEquals("Updated Name", existingItem.getName());
        assertEquals("Updated Description", existingItem.getDescription());
    }

    @Test
    void update_ShouldThrowException_WhenUserNotOwner() {
        Long itemId = 1L;
        Long notOwnerId = 2L;

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .build();

        User owner = new User(1L, "Owner", "owner@email.com");
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, updateDto, notOwnerId));
    }

    @Test
    void addComment_ShouldAddComment_WhenUserBookedItem() {
        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        User author = new User(userId, "User", "user@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(author)
                .status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Comment comment = Comment.builder()
                .text("Great item!")
                .author(author)
                .item(item)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                any(), any(), any(), any())).thenReturn(Optional.of(booking));
        when(commentRepository.existsByAuthorIdAndItemId(userId, itemId)).thenReturn(false);
        when(commentMapper.toComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(itemId, commentDto, userId);

        assertNotNull(result);
        verify(commentRepository).save(comment);
    }

    @Test
    void addComment_ShouldThrowException_WhenUserNeverBookedItem() {
        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        User author = new User(userId, "User", "user@email.com");
        Item item = Item.builder()
                .id(itemId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                any(), any(), any(), any())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.addComment(itemId, commentDto, userId));
    }

    @Test
    void search_ShouldReturnEmptyList_WhenTextIsBlank() {
        var result = itemService.search("   ");

        assertTrue(result.isEmpty());
    }
}