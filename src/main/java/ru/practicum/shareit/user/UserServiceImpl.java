package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса пользователей.
 * Использует репозиторий для доступа к данным и маппер для преобразования.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // Внедрение зависимости репозитория через конструктор
    private final UserRepository userRepository;

    /**
     * Создает пользователя, преобразуя DTO в Entity и обратно.
     */
    @Override
    public UserDto create(UserDto userDto) {
        // Проверка уникальности email
        userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .findFirst()
                .ifPresent(user -> {
                    throw new ConflictException("Email already exists: " + userDto.getEmail());
                });

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    /**
     * Находит пользователя по id, выбрасывает исключение если не найден.
     */
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return UserMapper.toUserDto(user);
    }

    /**
     * Возвращает всех пользователей, преобразуя Entity в DTO.
     */
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Частично обновляет пользователя.
     * Обновляет только те поля, которые не null в DTO.
     */
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            userRepository.findAll().stream()
                    .filter(user -> user.getEmail().equals(userDto.getEmail()))
                    .findFirst()
                    .ifPresent(user -> {
                        throw new ConflictException("Email already exists: " + userDto.getEmail());
                    });
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.update(existingUser));
    }

    /**
     * Удаляет пользователя по Id.
     */
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}
