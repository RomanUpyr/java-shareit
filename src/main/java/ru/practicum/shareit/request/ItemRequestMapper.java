package ru.practicum.shareit.request;

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
public class ItemRequestMapper {
    /**
     * Преобразует Entity запроса в DTO для возврата клиенту.
     *
     * @param itemRequest Entity запроса из базы данных.
     * @return ItemRequestDto объект для передачи клиенту.
     */
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getCreated()
        );
    }

    /**
     * Преобразует DTO запроса в Entity для сохранения в базу данных.
     *
     * @param itemRequestDto DTO запроса от клиента.
     * @param userRepository репозиторий для поиска пользователя.
     * @return ItemRequest Entity для сохранения.
     * @throws NotFoundException если пользователь не найден.
     */
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, UserRepository userRepository) {
        User requestor = userRepository.findById(itemRequestDto.getRequestorId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + itemRequestDto.getRequestorId()));

        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestorId(),
                itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now()
        );
    }
}
