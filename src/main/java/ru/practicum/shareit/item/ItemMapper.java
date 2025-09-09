package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Маппер для преобразования между Entity и DTO объектов вещи.
 * Обеспечивает согласованное преобразование данных между слоями.
 * Методы создадим статические, чтобы использовать маппер без создания экземпляра.
 */
@Component
public class ItemMapper {
    /**
     * Преобразует Entity вещи в DTO для возврата клиенту.
     *
     * @param item Entity вещи из базы данных
     * @return ItemDto объект для передачи клиенту
     */
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    /**
     * Преобразует DTO вещи в Entity для сохранения в базу данных.
     * Устанавливает запрос, если указан requestId
     *
     * @param itemDto DTO вещи от клиента
     * @return Item Entity для сохранения
     */
    public static Item toItem(ItemDto itemDto, ItemRequestRepository itemRequestRepository) {
        Item item = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,            // Владелец будет устанавливаться в сервисе на основе userId из заголовка
                null                    // Запрос устанавливается ниже
        );

        // Устанавливаем запрос, если указан requestId
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request not found with id: " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }

        return item;
    }
}
