package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса вещей.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    // Внедрение зависимости репозитория через конструктор
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    /**
     * Создает вещь, предварительно проверив существование владельца
     */
    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    /**
     * Находит вещь по идентификатору.
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
        return itemMapper.toItemDto(item);
    }

    /**
     * Находит все вещи определенного владельца.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет вещь с проверкой прав доступа.
     */
    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        // Проверка, что пользователь является владельцем вещи
        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        // Частичное обновление: только не-null поля
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    /**
     * Удаляет вещь.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Ищет доступные вещи по тексту.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }


}
