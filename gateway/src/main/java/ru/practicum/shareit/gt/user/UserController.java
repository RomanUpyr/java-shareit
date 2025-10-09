package ru.practicum.shareit.gt.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.gt.user.dto.UserDto;

/**
 * REST контроллер для работы с пользователями.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    /**
     * Создает нового пользователя.
     *
     * @param userDto данные пользователя из тела запроса.
     * @return созданный пользователь.
     */
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }


    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя из пути URL.
     * @return пользователь.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id      идентификатор пользователя для обновления.
     * @param userDto данные для обновления.
     * @return обновленный пользователь.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        return userClient.update(id, userDto);
    }

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор пользователя для удаления.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return userClient.delete(id);
    }

}
