package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gt.booking.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient{
    private static final String API_PREFIX = "/bookings";

    public BookingClient(WebClient webClient) {
        super(webClient);
    }

    public ResponseEntity<Object> create(BookingDto bookingDto, Long bookerId) {
        return post(API_PREFIX , bookerId, bookingDto);
    }

    public ResponseEntity<Object> approve(Long bookingId, Long ownerId, boolean approved) {
       Map<String, Object> parameters = Map.of("approved", approved);
        return patch(API_PREFIX + "/" + bookingId , ownerId, parameters);
    }

    public ResponseEntity<Object> getById(Long bookingId, Long userId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByBookerId(Long bookerId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(API_PREFIX, bookerId, parameters);
    }

    public ResponseEntity<Object> getByOwnerId(Long ownerId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(API_PREFIX + "/owner", ownerId, parameters);
    }

    public ResponseEntity<Object> update(Long bookingId, BookingDto bookingDto, Long userId) {
        return put(API_PREFIX + "/" + bookingId, userId, bookingDto);
    }

    public ResponseEntity<Object> cancel(Long bookingId, Long userId) {
        return delete(API_PREFIX + "/" + bookingId, userId);
    }
}
