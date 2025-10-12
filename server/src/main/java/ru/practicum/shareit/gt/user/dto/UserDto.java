package ru.practicum.shareit.gt.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Используется для обмена данными между клиентом и сервером
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    /**
     * Уникальный идентификатор пользователя
     */
    private Long id;

    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Адрес электронной почты пользователя.
     */
    private String email;
}
