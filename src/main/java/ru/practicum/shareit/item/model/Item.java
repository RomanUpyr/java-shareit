package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

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
    @NotBlank(message = "Item Id не должен быть пустым")
    private Long id;

    /**
     * Краткое название вещи.
     */
    @NotNull(message = "Item name не должен быть null")
    @NotBlank(message = "Item name не должен быть пустым")
    private String name;

    /**
     * Подробное описание вещи и её характеристик.
     */
    @NotNull(message = "Item description не должен быть null")
    @NotBlank(message = "Item description не должен быть пустым")
    private String description;

    /**
     * Статус доступности вещи для аренды (устанавливается владельцем вещи).
     * true - доступна, false - не доступна
     */
    @NotNull(message = "Item available не должен быть null")
    @NotBlank(message = "Item available не должен быть пустым")
    private Boolean available;

    /**
     * Владелец вещи - пользователь, который добавил вещь в систему.
     * Много вещей могут принадлежать одному пользователю (many-to-one).
     */
    @NotNull(message = "Owner не должен быть null")
    @NotBlank(message = "Owner не должен быть пустым")
    private User owner;

    /**
     * Ссылка на запрос, если вещь была добавлена в ответ на запрос другого пользователя.
     * Может быть null, если вещь добавлена без запроса.
     */
    private ItemRequest request;

}
