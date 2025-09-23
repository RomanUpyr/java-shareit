package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса вещей.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


    /**
     * Создает вещь, предварительно проверив существование владельца
     */
    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    /**
     * Находит вещь по идентификатору с дополнительной информацией о бронированиях и комментариях..
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        ItemDto itemDto = itemMapper.toItemDto(item);

        // Добавляем информацию о бронированиях только для владельца
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(findLastBooking(id));
            itemDto.setNextBooking(findNextBooking(id));
        }

        // Добавляем комментарии для всех пользователей
        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(id)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        return itemDto;

    }

    /**
     * Находит все вещи определенного владельца с информацией о бронированиях и комментариях.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        // Загружаем комментарии для всех вещей одним запросом
        Map<Long, List<CommentDto>> commentsByItemId = getCommentsByItemIds(itemIds);

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);

                    // Добавляем информацию о бронированиях для владельца
                    itemDto.setLastBooking(findLastBooking(item.getId()));
                    itemDto.setNextBooking(findNextBooking(item.getId()));

                    // Добавляем комментарии
                    itemDto.setComments(commentsByItemId.getOrDefault(item.getId(), Collections.emptyList()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Обновляет вещь с проверкой прав доступа.
     */
    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        // Проверка, что пользователь является владельцем вещи
        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        // Частичное обновление: только не-null поля
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    /**
     * Удаляет вещь.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Ищет доступные вещи по тексту.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавляет комментарий к вещи.
     */
    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        boolean hasBooked = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()).isPresent();

        if (!hasBooked) {
            throw new ValidationException("User can only comment on items they have booked in the past");
        }

        if (commentRepository.existsByAuthorIdAndItemId(userId, itemId)) {
            throw new ValidationException("User has already commented on this item");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }

    /**
     * Находит последнее завершенное бронирование для вещи.
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDto.BookingInfoDto findLastBooking(Long itemId) {
        Optional<Booking> lastBooking = bookingRepository
                .findCompletedBookingsByItemId(itemId, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .findFirst();

        return lastBooking.map(booking -> ItemDto.BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .end(booking.getEnd().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build()).orElse(null);
    }

    /**
     * Находит ближайшее следующее бронирование для вещи.
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDto.BookingInfoDto findNextBooking(Long itemId) {
        Optional<Booking> nextBooking = bookingRepository
                .findFutureBookingsByItemId(itemId, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .findFirst();

        return nextBooking.map(booking -> ItemDto.BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .end(booking.getEnd().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build()).orElse(null);
    }

    /**
     * Вспомогательный метод для получения комментариев по списку идентификаторов вещей.
     */
    private Map<Long, List<CommentDto>> getCommentsByItemIds(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return commentRepository.findByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())
                ));
    }
}
