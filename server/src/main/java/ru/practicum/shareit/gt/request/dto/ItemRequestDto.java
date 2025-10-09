package ru.practicum.shareit.gt.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.gt.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long requestorId;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически на сервере
     */
    //@NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;


    /**
     * Список вещей, созданных пользователями в ответ на этот запрос.
     * Может быть пустым, если на запрос еще не ответили.
     */
    private List<ItemDto> items;

}
