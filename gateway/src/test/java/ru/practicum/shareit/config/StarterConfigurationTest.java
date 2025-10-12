package ru.practicum.shareit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.client.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class StarterConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void webClientBean_shouldBeConfigured() {
        WebClient webClient = applicationContext.getBean(WebClient.class);
        assertNotNull(webClient);
    }

    @Test
    void bookingClientBean_shouldBeConfigured() {
        BookingClient bookingClient = applicationContext.getBean(BookingClient.class);
        assertNotNull(bookingClient);
    }

    @Test
    void itemClientBean_shouldBeConfigured() {
        ItemClient itemClient = applicationContext.getBean(ItemClient.class);
        assertNotNull(itemClient);
    }

    @Test
    void userClientBean_shouldBeConfigured() {
        UserClient userClient = applicationContext.getBean(UserClient.class);
        assertNotNull(userClient);
    }

    @Test
    void itemRequestClientBean_shouldBeConfigured() {
        ItemRequestClient itemRequestClient = applicationContext.getBean(ItemRequestClient.class);
        assertNotNull(itemRequestClient);
    }

    @Test
    void baseClientDependencies_shouldBeInjected() {
        BookingClient bookingClient = applicationContext.getBean(BookingClient.class);
        assertNotNull(bookingClient.webClient);
    }
}