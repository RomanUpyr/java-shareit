package ru.practicum.shareit.gt.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    @NotNull
    private String name;

    /**
     * Адрес электронной почты пользователя.
     */
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен соответствовать формату email")
    private String email;
}
