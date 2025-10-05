package ru.practicum.shareit.server.user;

import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с пользователями.
 */
public interface UserService {
    /**
     * Создает нового пользователя
     *
     * @param userDto DTO с данными пользователя
     * @return созданный пользователь в формате DTO
     */
    UserDto create(UserDto userDto);

    /**
     * Находит пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return пользователь в формате DTO
     * @throws NotFoundException если пользователь не найден
     */
    UserDto getById(Long id);

    /**
     * Возвращает всех пользователей
     *
     * @return список всех пользователей в формате DTO
     */
    List<UserDto> getAll();

    /**
     * Обновляет данные пользователя
     *
     * @param id      идентификатор пользователя для обновления
     * @param userDto DTO с обновленными данными
     * @return обновленный пользователь в формате DTO
     * @throws NotFoundException если пользователь не найден
     */
    UserDto update(Long id, UserDto userDto);

    /**
     * Удаляет пользователя
     *
     * @param id идентификатор пользователя для удаления
     */
    void delete(Long id);
}
