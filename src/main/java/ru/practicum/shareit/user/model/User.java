package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель пользователя системы.
 * Содержит информацию о пользователе, который может владеть вещами и брать их в аренду.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @NotNull(message = "User id не должен быть null")
    private Long id;

    /**
     * Имя пользователя.
     */
    @NotNull(message = "User name не должен быть null")
    private String name;

    /**
     * Адрес электронной почты пользователя.
     */
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен соответствовать формату email")
    private String email;
}
