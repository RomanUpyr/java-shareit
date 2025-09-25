package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

/**
 * Маппер для преобразования между Entity и DTO объектов запроса вещи.
 */
@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final UserRepository userRepository;

    /**
     * Преобразует Entity запроса в DTO для возврата клиенту.
     *
     * @param itemRequest Entity запроса из базы данных.
     * @return ItemRequestDto объект для передачи клиенту.
     */
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
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
        User requestor = userRepository.findById(itemRequestDto.getRequestor().getId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + itemRequestDto.getRequestor().getId()));

        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now()
        );
    }
}
