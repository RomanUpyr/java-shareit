package ru.practicum.shareit.server.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

/**
 * Интерфейс репозитория для работы с запросами вещей.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    /**
     * Находит все запросы определенного пользователя, отсортированные по дате создания (новые сначала).
     */
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = :requesterId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    /**
     * Находит все запросы, созданные другими пользователями, отсортированные по дате создания (новые сначала).
     */
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long userId);

    /**
     * Находит все вещи, созданные в ответ на конкретный запрос.
     */
    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findByRequestId(Long requestId);
}
