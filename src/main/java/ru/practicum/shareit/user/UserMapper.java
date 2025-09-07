package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * Маппер для преобразования между Entity и DTO объектов пользователя.
 * Методы создадим статические, чтобы использовать маппер без создания экземпляра.
 */
public class UserMapper {
    /**
     * Преобразует Entity пользователя в DTO для возврата клиенту.
     *
     * @param user Entity пользователя из базы данных
     * @return UserDto объект для передачи клиенту
     */
    public static UserDto toUserDto(User user) {
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
    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
