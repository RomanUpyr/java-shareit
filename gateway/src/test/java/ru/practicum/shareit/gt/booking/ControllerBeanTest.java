package ru.practicum.shareit.gt.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.practicum.shareit.gt.item.ItemController;
import ru.practicum.shareit.gt.request.ItemRequestController;
import ru.practicum.shareit.gt.user.UserController;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ControllerBeanTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void bookingController_shouldBeConfigured() {
        BookingController controller = applicationContext.getBean(BookingController.class);
        assertNotNull(controller);
    }

    @Test
    void itemController_shouldBeConfigured() {
        ItemController controller = applicationContext.getBean(ItemController.class);
        assertNotNull(controller);
    }

    @Test
    void userController_shouldBeConfigured() {
        UserController controller = applicationContext.getBean(UserController.class);
        assertNotNull(controller);
    }

    @Test
    void itemRequestController_shouldBeConfigured() {
        ItemRequestController controller = applicationContext.getBean(ItemRequestController.class);
        assertNotNull(controller);
    }
}