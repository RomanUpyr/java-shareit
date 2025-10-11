package ru.practicum.shareit.gt.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.gt.item.dto.CommentDto;
import ru.practicum.shareit.gt.item.dto.ItemDto;


/**
 * REST контроллер для работы с вещами.
 * Использует кастомный заголовок X-Sharer-User-Id для идентификации пользователя.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    // Внедрение зависимости сервиса через конструктор
    private final ItemClient itemClient;

    // Имя заголовка для передачи идентификатора пользователя
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новую вещь от имени пользователя
     *
     * @param itemDto данные вещи из тела запроса
     * @param ownerId идентификатор владельца из заголовка X-Sharer-User-Id
     * @return созданная вещь
     */
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.create(itemDto, ownerId);
    }

    /**
     * Возвращает вещь по идентификатору (может быть вызван любым пользователем).
     *
     * @param id идентификатор вещи.
     * @return вещь.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getById(id, userId);
    }

    /**
     * Возвращает все вещи владельца.
     *
     * @param ownerId идентификатор владельца из заголовка.
     * @return список вещей владельца.
     */
    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.getByOwnerId(ownerId);
    }

    /**
     * Обновляет данные вещи.
     *
     * @param id      идентификатор вещи.
     * @param itemDto данные для обновления.
     * @param ownerId идентификатор пользователя из заголовка (для проверки прав).
     * @return обновленная вещь.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @PathVariable Long id,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.update(id, itemDto, ownerId);
    }

    /**
     * Удаляет вещь
     *
     * @param id идентификатор вещи для удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return itemClient.delete(id);
    }

    /**
     * Ищет доступные вещи по тексту
     *
     * @param text текст для поиска (из параметра запроса)
     * @return список подходящих вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    /**
     * Добавляет комментарий к вещи.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Validated @RequestBody CommentDto commentDto,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.addComment(itemId, commentDto, userId);
    }

}
