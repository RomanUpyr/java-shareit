package ru.practicum.shareit.gt.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gt.item.dto.CommentDto;
import ru.practicum.shareit.gt.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_ShouldReturnCreatedItem() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        ItemDto createdItem = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(ownerId)
                .build();

        when(itemService.create(any(ItemDto.class), eq(ownerId))).thenReturn(createdItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.ownerId").value(ownerId));
    }

    @Test
    void getById_ShouldReturnItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getByOwnerId_ShouldReturnItemsList() throws Exception {
        Long ownerId = 1L;

        ItemDto item1 = ItemDto.builder().id(1L).name("Item1").build();
        ItemDto item2 = ItemDto.builder().id(2L).name("Item2").build();

        when(itemService.getByOwnerId(ownerId)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void update_ShouldReturnUpdatedItem() throws Exception {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Name")
                .description("Updated Description")
                .available(true)
                .build();

        when(itemService.update(eq(itemId), any(ItemDto.class), eq(ownerId))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isOk());
    }

    @Test
    void search_ShouldReturnFoundItems() throws Exception {
        String searchText = "tool";

        ItemDto item1 = ItemDto.builder().id(1L).name("Power Tool").build();
        ItemDto item2 = ItemDto.builder().id(2L).name("Hand Tool").build();

        when(itemService.search(searchText)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Power Tool"))
                .andExpect(jsonPath("$[1].name").value("Hand Tool"));
    }

    @Test
    void search_ShouldReturnEmptyList_WhenTextIsBlank() throws Exception {
        when(itemService.search("")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        CommentDto createdComment = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("User")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(eq(itemId), any(CommentDto.class), eq(userId)))
                .thenReturn(createdComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("User"));
    }
}