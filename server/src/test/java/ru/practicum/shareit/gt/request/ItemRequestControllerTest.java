package ru.practicum.shareit.gt.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gt.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create_ShouldReturnCreatedRequest() throws Exception {
        Long userId = 1L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a power drill");

        ItemRequestDto createdRequest = new ItemRequestDto();
        createdRequest.setId(1L);
        createdRequest.setDescription("Need a power drill");
        createdRequest.setRequestorId(userId);
        createdRequest.setCreated(LocalDateTime.now());

        when(itemRequestService.create(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(createdRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a power drill"))
                .andExpect(jsonPath("$.requestorId").value(userId));
    }


    @Test
    void getByRequestorId_ShouldReturnUserRequests() throws Exception {
        Long userId = 1L;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("First request");
        request1.setRequestorId(userId);

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Second request");
        request2.setRequestorId(userId);

        when(itemRequestService.getByUserId(userId)).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("First request"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Second request"));
    }

    @Test
    void getAllExceptUser_ShouldReturnOtherUsersRequests() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemRequestDto otherRequest = new ItemRequestDto();
        otherRequest.setId(3L);
        otherRequest.setDescription("Other user request");
        otherRequest.setRequestorId(2L);

        when(itemRequestService.getAllExceptUser(userId, from, size))
                .thenReturn(List.of(otherRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].requestorId").value(2L));
    }

    @Test
    void getAllExceptUser_ShouldUseDefaultPagination() throws Exception {
        Long userId = 1L;

        when(itemRequestService.getAllExceptUser(userId, 0, 10))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_ShouldReturnRequest() throws Exception {
        Long requestId = 1L;

        ItemRequestDto request = new ItemRequestDto();
        request.setId(requestId);
        request.setDescription("Specific request");
        request.setRequestorId(1L);
        request.setCreated(LocalDateTime.now());

        when(itemRequestService.getById(requestId)).thenReturn(request);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Specific request"));
    }

    @Test
    void update_ShouldReturnUpdatedRequest() throws Exception {
        Long requestId = 1L;

        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        ItemRequestDto updatedRequest = new ItemRequestDto();
        updatedRequest.setId(requestId);
        updatedRequest.setDescription("Updated description");
        updatedRequest.setRequestorId(1L);

        when(itemRequestService.update(eq(requestId), any(ItemRequestDto.class)))
                .thenReturn(updatedRequest);

        mockMvc.perform(patch("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(delete("/requests/{requestId}", requestId))
                .andExpect(status().isOk());
    }
}