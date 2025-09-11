package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями.
 */
public interface UserRepository {
    /**
     * Сохраняет нового пользователя
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь с присвоенным id
     */
    User save(User user);

    /**
     * Находит пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findById(Long id);

    /**
     * Возвращает всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Обновляет данные пользователя
     *
     * @param user пользователь с обновленными данными
     * @return обновленный пользователь
     */
    User update(User user);

    /**
     * Удаляет пользователя по идентификатору
     *
     * @param id идентификатор пользователя для удаления
     */
    void deleteById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    List<User> findByName(String name);
}
