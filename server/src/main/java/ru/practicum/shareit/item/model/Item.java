package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

/**
 * Модель вещи (item) в системе шеринга, объект, который можно арендовать.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    /**
     * Уникальный идентификатор вещи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое название вещи.
     */
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Item name не должен быть пустым")
    private String name;

    /**
     * Подробное описание вещи и её характеристик.
     */
    @Column(name = "description", length = 1000)
    @NotBlank(message = "Item description не должен быть пустым")
    private String description;

    /**
     * Статус доступности вещи для аренды (устанавливается владельцем вещи).
     * True - доступна, false - не доступна
     */
    @Column(name = "is_available", nullable = false)
    @NotNull(message = "Item available не должен быть null")
    private Boolean available;

    /**
     * Владелец вещи - пользователь, который добавил вещь в систему.
     * Много вещей могут принадлежать одному пользователю (many-to-one).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = "Owner не должен быть null")
    private User owner;

    /**
     * Ссылка на запрос, если вещь была добавлена в ответ на запрос другого пользователя.
     * Может быть null, если вещь добавлена без запроса.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
