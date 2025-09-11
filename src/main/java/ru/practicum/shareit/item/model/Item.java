package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Модель вещи (item) в системе шеринга, объект, который можно арендовать.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    /**
     * Уникальный идентификатор вещи.
     */
    @NotNull(message = "Item Id не должен быть null")
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
     * true - доступна, false - не доступна
     */
    @NotNull(message = "Item available не должен быть null")
    private Boolean available;

    /**
     * Владелец вещи - пользователь, который добавил вещь в систему.
     * Много вещей могут принадлежать одному пользователю (many-to-one).
     */
    @NotNull(message = "Owner не должен быть null")
    private User owner;

    /**
     * Ссылка на запрос, если вещь была добавлена в ответ на запрос другого пользователя.
     * Может быть null, если вещь добавлена без запроса.
     */
    private ItemRequest request;
}
