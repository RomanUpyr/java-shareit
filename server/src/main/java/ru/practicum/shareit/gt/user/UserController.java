package ru.practicum.shareit.gt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gt.user.dto.UserDto;

import java.util.List;

/**
 * REST контроллер для работы с пользователями.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создает нового пользователя.
     *
     * @param userDto данные пользователя из тела запроса.
     * @return созданный пользователь.
     */
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }


    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя из пути URL.
     * @return пользователь.
     */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список всех пользователей.
     */
    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id      идентификатор пользователя для обновления.
     * @param userDto данные для обновления.
     * @return обновленный пользователь.
     */
    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор пользователя для удаления.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
