package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;

/**
 * Маппер для преобразования между Entity и DTO объектов вещи.
 * Обеспечивает согласованное преобразование данных между слоями.
 * Методы создадим статические, чтобы использовать маппер без создания экземпляра.
 */
@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final ItemRequestRepository itemRequestRepository;

    /**
     * Преобразует Entity вещи в DTO для возврата клиенту.
     *
     * @param item Entity вещи из базы данных
     * @return ItemDto объект для передачи клиенту
     */
    public ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
    }

    /**
     * Преобразует DTO вещи в Entity для сохранения в базу данных.
     * Устанавливает запрос, если указан requestId
     *
     * @param itemDto DTO вещи от клиента
     * @return Item Entity для сохранения
     */
    public Item toItem(ItemDto itemDto) {
        Item.ItemBuilder itemBuilder = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(null)
                .request(null);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request not found with id: " + itemDto.getRequestId()));
            itemBuilder.request(itemRequest);
        }

        return itemBuilder.build();
    }

}
