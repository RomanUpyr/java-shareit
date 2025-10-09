package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.util.Map;

@Component
@Slf4j
public abstract class BaseClient {
    protected final WebClient webClient;

    public BaseClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return makeRequest(HttpMethod.GET, path, userId, null, parameters);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return makeRequest(HttpMethod.POST, path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> put(String path, Long userId, T body) {
        return makeRequest(HttpMethod.PUT, path, userId, body, null);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return patch(path, userId, body, null);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Map<String, Object> parameters) {
        return patch(path, userId, null, parameters);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body, Map<String, Object> parameters) {
        return webClient.method(HttpMethod.PATCH)
                .uri(uriBuilder -> {
                    UriBuilder uriBuilderInstance = uriBuilder.path(path);
                    if (parameters != null) {
                        parameters.forEach(uriBuilderInstance::queryParam);
                    }
                    return uriBuilderInstance.build();
                })
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .bodyValue(body != null ? body : "")
                .retrieve()
                .toEntity(Object.class)
                .block();
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeRequest(HttpMethod.DELETE, path, userId, null, null);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null);
    }

    private <T> ResponseEntity<Object> makeRequest(HttpMethod method, String path, Long userId, T body, Map<String, Object> parameters) {
        try {
            return webClient.method(method)
                    .uri(uriBuilder -> {
                        UriBuilder uriBuilderInstance = uriBuilder.path(path);
                        if (parameters != null) {
                            parameters.forEach(uriBuilderInstance::queryParam);
                        }
                        return uriBuilderInstance.build();
                    })
                    .header("X-Sharer-User-Id", userId != null ? String.valueOf(userId) : "")
                    .bodyValue(body != null ? body : "")
                    .retrieve()
                    .toEntity(Object.class)
                    .block();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAs(String.class));
        }
    }
}
