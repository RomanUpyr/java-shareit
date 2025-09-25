package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Модель пользователя системы.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
    @NotNull(message = "User name не должен быть null")
    private String name;

    /**
     * Адрес электронной почты пользователя.
     */
    @Column(name = "email", nullable = false, unique = true, length = 512)
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен соответствовать формату email")
    private String email;
}
