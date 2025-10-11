package ru.practicum.shareit.gt.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gt.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void create_ShouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", "john@email.com");
        UserDto createdUser = new UserDto(1L, "John Doe", "john@email.com");

        when(userService.create(any(UserDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@email.com"));
    }

    @Test
    void getById_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "John Doe", "john@email.com");

        when(userService.getById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@email.com"));
    }

    @Test
    void getAll_ShouldReturnAllUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "John Doe", "john@email.com");
        UserDto user2 = new UserDto(2L, "Jane Smith", "jane@email.com");

        when(userService.getAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void update_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 1L;
        UserDto updateDto = new UserDto(userId, "John Updated", "john.updated@email.com");
        UserDto updatedUser = new UserDto(userId, "John Updated", "john.updated@email.com");

        when(userService.update(eq(userId), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@email.com"));
    }

    @Test
    void update_ShouldHandlePartialUpdate() throws Exception {
        Long userId = 1L;
        UserDto partialUpdate = new UserDto();
        partialUpdate.setName("Only Name Updated");

        UserDto updatedUser = new UserDto(userId, "Only Name Updated", "original@email.com");

        when(userService.update(eq(userId), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.email").value("original@email.com"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
    }

}