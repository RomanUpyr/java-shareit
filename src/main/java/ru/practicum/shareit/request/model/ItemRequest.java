package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель запроса вещи.
 * Пользователь создает запрос, если не находит нужную вещь в системе.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст запроса с описанием требуемой вещи.
     */
    @Column(name = "description", nullable = false, length = 1000)
    @NotNull(message = "ItemRequest description не должен быть null")
    @NotBlank(message = "ItemRequest description не должен быть пустым")
    private String description;

    /**
     * Пользователь, создавший запрос.
     * Много запросов могут быть от одного пользователя(связь many-to-one).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @NotNull(message = "ItemRequest requestor не должен быть null")
    private User requestor;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически при создании.
     */
    @Column(name = "created", nullable = false)
    @NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;
}
