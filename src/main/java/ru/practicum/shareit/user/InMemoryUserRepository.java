package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

/**
 * In-memory реализация репозитория пользователей.
 * Хранит данные в HashMap в памяти приложения.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    // Хранилище пользователей: id -> User
    private final Map<Long, User> users = new HashMap<>();
    // Счетчик для генерации уникальных идентификаторов
    private long nextId = 1L;

    /**
     * Сохраняет пользователя, присваивая уникальный id
     */
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Находит пользователя по id
     */
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Возвращает всех пользователей
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Обновляет данные существующего пользователя
     */
    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Удаляет пользователя по id
     */
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    /**
     * Проверяет существование пользователя с указанным email
     */
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Находит пользователя по email
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Проверяет существование пользователя с указанным id
     */
    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    /**
     * Находит всех пользователей с указанным именем
     */
    @Override
    public List<User> findByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .toList();
    }


}
