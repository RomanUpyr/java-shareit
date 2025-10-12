package ru.practicum.shareit.gt.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.AbstractIntegrationTest;
import ru.practicum.shareit.gt.item.dto.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ItemValidationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createItem_withBlankName_shouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Valid Description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withNullName_shouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(null);
        itemDto.setDescription("Valid Description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withBlankDescription_shouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Valid Name");
        itemDto.setDescription("");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withNullAvailable_shouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Valid Name");
        itemDto.setDescription("Valid Description");
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }
}