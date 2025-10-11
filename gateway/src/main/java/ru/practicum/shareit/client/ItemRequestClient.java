package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gt.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(WebClient webClient) {
        super(webClient);
    }

    public ResponseEntity<Object> create(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        return post(API_PREFIX , userId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getByUserId(Long userId) {
        return get(API_PREFIX , userId);
    }

    public ResponseEntity<Object> getAllExceptUser(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "/all", userId, parameters);
    }

    public ResponseEntity<Object> getById(Long requestId) {
        return get(API_PREFIX + "/" + requestId);
    }

    public ResponseEntity<Object> update(Long requestId, Object itemRequestDto) {
        return patch(API_PREFIX + "/" + requestId, null, itemRequestDto);
    }

    public ResponseEntity<Object> delete(Long requestId) {
        return delete(API_PREFIX + "/" + requestId);
    }
}
