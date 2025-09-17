package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Используется для обмена данными между клиентом и сервером
 * (для передачи данных о пользователе).
 * Отделяет модель данных от представления, возвращаемого через API.
 * Создадим его для целостного представления модели, чтобы у каждой сущности был DTO.
 * Валидация в принципе происходит на уровне User, на всякий случай сюда тоже добавим,
 * потом уберем, если что.
 */
@Data
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
     * Должен быть уникальным в системе.
     * Валидируется на соответствие формату email и не может быть пустым.
     */
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен соответствовать формату email")
    private String email;
}
