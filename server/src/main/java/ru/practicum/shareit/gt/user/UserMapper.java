package ru.practicum.shareit.gt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.gt.user.dto.UserDto;
import ru.practicum.shareit.gt.user.model.User;

/**
 * Маппер для преобразования между Entity и DTO объектов пользователя.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    /**
     * Преобразует Entity пользователя в DTO для возврата клиенту.
     *
     * @param user Entity пользователя из базы данных
     * @return UserDto объект для передачи клиенту
     */
    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    /**
     * Преобразует DTO пользователя в Entity для сохранения в базу данных.
     *
     * @param userDto DTO пользователя от клиента
     * @return User Entity для сохранения
     */
    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
