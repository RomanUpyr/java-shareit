package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gt.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient<ItemRequestDto> {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(WebClient webClient) {
        super(webClient, ItemRequestDto.class);
    }

    public ResponseEntity<ItemRequestDto> create(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        return webClient.post()
                .uri(API_PREFIX)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(itemRequestCreateDto)
                .retrieve()
                .toEntity(ItemRequestDto.class)
                .block();
    }

    public ResponseEntity<ItemRequestDto> getByUserId(Long userId) {
        return get(API_PREFIX , userId);
    }

    public ResponseEntity<ItemRequestDto> getAllExceptUser(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "/all", userId, parameters);
    }

    public ResponseEntity<ItemRequestDto> getById(Long requestId) {
        return get(API_PREFIX + "/" + requestId);
    }

    public ResponseEntity<ItemRequestDto> update(Long requestId, ItemRequestDto itemRequestDto) {
        return patch(API_PREFIX + "/" + requestId, null, itemRequestDto);
    }

    public ResponseEntity<ItemRequestDto> delete(Long requestId) {
        return delete(API_PREFIX + "/" + requestId);
    }
}
