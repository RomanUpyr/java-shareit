package ru.practicum.shareit.gt.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;
import ru.practicum.shareit.gt.request.dto.ItemRequestCreateDto;

/**
 * REST контроллер для работы с запросами вещей.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    // Внедрение зависимости сервиса через конструктор
    private final ItemRequestClient itemRequestClient;

    // Имя заголовка для передачи идентификатора пользователя
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новый запрос на вещь.
     *
     * @param itemRequestCreateDto данные запроса из тела запроса.
     * @param userId               идентификатор пользователя из заголовка X-Sharer-User-Id.
     * @return созданный запрос.
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequestCreateDto.getDescription());
        return itemRequestClient.create(itemRequestCreateDto, userId);
    }

    /**
     * Возвращает все запросы текущего пользователя.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param userId идентификатор пользователя из заголовка.
     * @return список запросов пользователя.
     */
    @GetMapping
    public ResponseEntity<ItemRequestDto> getByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getByUserId(userId);
    }

    /**
     * Возвращает все запросы других пользователей.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param userId идентификатор пользователя из заголовка.
     * @return список запросов других пользователей.
     */
    @GetMapping("/all")
    public ResponseEntity<ItemRequestDto> getAllExceptUser(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return itemRequestClient.getAllExceptUser(userId, from, size);
    }

    /**
     * Возвращает конкретный запрос по идентификатору.
     *
     * @param requestId идентификатор запроса.
     * @return запрос.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getById(@PathVariable Long requestId) {
        return itemRequestClient.getById(requestId);
    }

    /**
     * Обновляет данные запроса.
     *
     * @param requestId      идентификатор запроса
     * @param itemRequestDto данные для обновления
     * @return обновленный запрос
     */
    @PatchMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> update(@Valid @PathVariable Long requestId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.update(requestId, itemRequestDto);
    }

    /**
     * Удаляет запрос.
     *
     * @param requestId идентификатор запроса для удаления.
     */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> delete(@PathVariable Long requestId) {
        return itemRequestClient.delete(requestId);
    }
}
