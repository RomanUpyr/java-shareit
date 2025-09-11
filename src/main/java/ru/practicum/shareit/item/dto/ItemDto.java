package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных о вещи.
 * Содержит только те поля, которые необходимы для клиент-серверного взаимодействия.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    /**
     * Уникальный идентификатор вещи.
     */
    private Long id;

    /**
     * Краткое название вещи.
     */
    @NotBlank(message = "Item name не должен быть пустым")
    private String name;

    /**
     * Подробное описание вещи и её характеристик.
     */
    @NotBlank(message = "Item description не должен быть пустым")
    private String description;

    /**
     * Статус доступности вещи для аренды (устанавливается владельцем вещи).
     */
    @NotNull(message = "Item available не должен быть null")
    private Boolean available;

    /**
     * Ссылка на запрос, если вещь была добавлена в ответ на запрос другого пользователя.
     * Может быть null, если вещь добавлена без запроса.
     */
    private Long requestId;
}
