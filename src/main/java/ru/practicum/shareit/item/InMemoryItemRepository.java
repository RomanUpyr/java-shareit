package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория вещей.
 * Использует HashMap для хранения данных и Stream API для поиска.
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    // Хранилище вещей: id -> Item
    private final Map<Long, Item> items = new HashMap<>();
    // Счетчик для генерации уникальных идентификаторов
    private long nextId = 1L;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    /**
     * Находит вещи по идентификатору владельца.
     * Использует Stream API для фильтрации.
     */
    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    /**
     * Ищет доступные вещи по тексту в названии или описании.
     * Игнорирует регистр и пустые запросы.
     */
    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .collect(Collectors.toList());
    }


}
