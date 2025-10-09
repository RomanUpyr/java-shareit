package ru.practicum.shareit.client;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gt.item.dto.ItemDto;
import ru.practicum.shareit.gt.item.dto.CommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(WebClient webClient) {
        super(webClient);
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Long ownerId) {
        return post(API_PREFIX , ownerId, itemDto);
    }

    public ResponseEntity<Object> getById(Long id, Long userId) {
        return get(API_PREFIX + "/" + id, userId);
    }

    public ResponseEntity<Object> getByOwnerId(Long ownerId) {
        return get(API_PREFIX , ownerId);
    }

    public ResponseEntity<Object> update(Long id, ItemDto itemDto, Long ownerId) {
        return patch(API_PREFIX + "/" + id, ownerId, itemDto);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete(API_PREFIX + "/" + id);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get(API_PREFIX + "/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, CommentDto commentDto, Long userId) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto);
    }
}
