package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с вещами.
 */
public interface ItemRepository {
    /**
     * Сохраняет новую вещь
     */
    Item save(Item item);

    /**
     * Находит вещь по идентификатору
     */
    Optional<Item> findById(Long id);

    /**
     * Возвращает все вещи
     */
    List<Item> findAll();

    /**
     * Находит все вещи определенного владельца
     * @param ownerId идентификатор владельца
     * @return список вещей владельца
     */
    List<Item> findByOwnerId(Long ownerId);

    /**
     * Ищет вещи по тексту в названии или описании.
     * Возвращает только доступные для аренды вещи.
     * @param text текст для поиска
     * @return список подходящих вещей
     */
    List<Item> search(String text);

    /**
     * Обновляет данные вещи
     */
    Item update(Item item);

    /**
     * Удаляет вещь по идентификатору
     */
    void deleteById(Long id);
}
