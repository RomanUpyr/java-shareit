package ru.practicum.shareit.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    List<User> findByName(String name);
}
