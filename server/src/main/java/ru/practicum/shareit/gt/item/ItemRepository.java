package ru.practicum.shareit.gt.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.gt.item.model.Item;

import java.util.List;

/**
 * Интерфейс репозитория для работы с вещами.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Находит все вещи определенного владельца
     *
     * @param ownerId идентификатор владельца
     * @return список вещей владельца
     */
    List<Item> findByOwnerId(Long ownerId);

    /**
     * Ищет вещи по тексту в названии или описании.
     * Возвращает только доступные для аренды вещи.
     *
     * @param text текст для поиска
     * @return список подходящих вещей
     */
    @Query("SELECT i FROM Item i WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> search(@Param("text") String text);

    /**
     * Находит все вещи, созданные в ответ на конкретный запрос.
     *
     * @param requestId идентификатор запроса
     * @return список вещей, созданных с указанием данного requestId
     */
    List<Item> findByRequestId(Long requestId);

}
