package ru.practicum.shareit.gt.request;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.item.ItemMapper;
import ru.practicum.shareit.gt.item.dto.ItemDto;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;
import ru.practicum.shareit.gt.request.model.ItemRequest;
import ru.practicum.shareit.gt.user.model.User;
import ru.practicum.shareit.gt.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между Entity и DTO объектов запроса вещи.
 */
@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Lazy
    private final ItemMapper itemMapper;
    /**
     * Преобразует Entity запроса в DTO для возврата клиенту.
     *
     * @param itemRequest Entity запроса из базы данных.
     * @return ItemRequestDto объект для передачи клиенту.
     */
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        List<ItemDto> items = getItemsForRequest(itemRequest.getId());

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                items
        );
    }

    /**
     * Преобразует DTO запроса в Entity для сохранения в базу данных.
     *
     * @param itemRequestDto DTO запроса от клиента.
     * @return ItemRequest Entity для сохранения.
     * @throws NotFoundException если пользователь не найден.
     */
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(itemRequestDto.getRequestorId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + itemRequestDto.getRequestorId()));

        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now()
        );
    }

    private List<ItemDto> getItemsForRequest(Long requestId) {
        try {
            return itemRequestRepository.findByRequestId(requestId).stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // В случае ошибки возвращаем пустой список
            return Collections.emptyList();
        }
    }
}
