package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о запросе вещи.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    /**
     * Уникальный идентификатор запроса.
     */
    private Long id;

    /**
     * Текст запроса с описанием требуемой вещи/
     * Не может быть пустым/
     */
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    @NotNull(message = "ItemRequest requestor не должен быть null")
    private User requestor;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически на сервере
     */
    @NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;

}
