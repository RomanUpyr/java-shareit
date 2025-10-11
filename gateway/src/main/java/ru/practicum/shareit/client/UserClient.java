package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gt.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(WebClient webClient) {
        super(webClient);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(API_PREFIX, userDto);
    }

    public ResponseEntity<Object> getById(Long id) {
        return get(API_PREFIX + "/" + id);
    }

    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX);
    }

    public ResponseEntity<Object> update(Long id, UserDto userDto) {
        return patch(API_PREFIX + "/" + id, null, userDto);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete(API_PREFIX + "/" + id);
    }
}
