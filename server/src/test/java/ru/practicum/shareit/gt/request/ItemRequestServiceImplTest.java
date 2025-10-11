package ru.practicum.shareit.gt.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.item.ItemMapper;
import ru.practicum.shareit.gt.item.ItemRepository;
import ru.practicum.shareit.gt.item.dto.ItemDto;
import ru.practicum.shareit.gt.item.model.Item;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;
import ru.practicum.shareit.gt.request.model.ItemRequest;
import ru.practicum.shareit.gt.user.UserRepository;
import ru.practicum.shareit.gt.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("user" + id + "@email.com")
                .build();
    }

    private ItemRequest createTestItemRequest(Long id, User requestor) {
        return ItemRequest.builder()
                .id(id)
                .description("Test description for request " + id)
                .requestor(requestor)
                .created(LocalDateTime.now().minusDays(1))
                .build();
    }

    private Item createTestItem(Long id, User owner, ItemRequest request) {
        return Item.builder()
                .id(id)
                .name("Item " + id)
                .description("Description for item " + id)
                .available(true)
                .owner(owner)
                .request(request)
                .build();
    }

    @Test
    void create_ShouldCreateItemRequest_WhenValidData() {
        Long userId = 1L;
        User requestor = createTestUser(userId);

        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Need a power drill");

        ItemRequest itemRequest = createTestItemRequest(null, requestor);
        itemRequest.setDescription("Need a power drill");

        ItemRequest savedRequest = createTestItemRequest(1L, requestor);
        savedRequest.setDescription("Need a power drill");

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(1L);
        expectedDto.setDescription("Need a power drill");
        expectedDto.setRequestorId(userId);
        expectedDto.setCreated(savedRequest.getCreated());
        expectedDto.setItems(Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);
        when(itemRequestMapper.toItemRequestDto(savedRequest)).thenReturn(expectedDto);

        ItemRequestDto result = itemRequestService.create(inputDto, userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a power drill", result.getDescription());
        assertEquals(userId, result.getRequestorId());
        assertTrue(result.getItems().isEmpty());

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));
        verify(itemRequestMapper).toItemRequestDto(savedRequest);
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Test description");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.create(inputDto, userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getById_ShouldReturnItemRequest_WhenExists() {
        Long requestId = 1L;
        User requestor = createTestUser(1L);
        ItemRequest itemRequest = createTestItemRequest(requestId, requestor);

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(requestId);
        expectedDto.setDescription(itemRequest.getDescription());
        expectedDto.setRequestorId(requestor.getId());
        expectedDto.setCreated(itemRequest.getCreated());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(expectedDto);
        when(itemRepository.findByRequestId(requestId)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper).toItemRequestDto(itemRequest);
        verify(itemRepository).findByRequestId(requestId);
    }

    @Test
    void getById_ShouldThrowException_WhenRequestNotFound() {
        Long requestId = 999L;

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(requestId));

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper, never()).toItemRequestDto(any());
    }

    @Test
    void getById_ShouldIncludeItems_WhenItemsExist() {
        Long requestId = 1L;
        User requestor = createTestUser(1L);
        User owner = createTestUser(2L);

        ItemRequest itemRequest = createTestItemRequest(requestId, requestor);
        Item item = createTestItem(1L, owner, itemRequest);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .ownerId(owner.getId())
                .requestId(requestId)
                .build();

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(requestId);
        expectedDto.setItems(List.of(itemDto));

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(expectedDto);
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemRequestDto result = itemRequestService.getById(requestId);

        assertNotNull(result);
        assertFalse(result.getItems().isEmpty());
        assertEquals(1, result.getItems().size());
        assertEquals(itemDto.getId(), result.getItems().get(0).getId());

        verify(itemRepository).findByRequestId(requestId);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void getByUserId_ShouldReturnUserRequests() {
        Long userId = 1L;
        User requestor = createTestUser(userId);

        ItemRequest request1 = createTestItemRequest(1L, requestor);
        ItemRequest request2 = createTestItemRequest(2L, requestor);

        ItemRequestDto dto1 = new ItemRequestDto();
        dto1.setId(1L);
        dto1.setDescription("Request 1");

        ItemRequestDto dto2 = new ItemRequestDto();
        dto2.setId(2L);
        dto2.setDescription("Request 2");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1, request2));
        when(itemRequestMapper.toItemRequestDto(request1)).thenReturn(dto1);
        when(itemRequestMapper.toItemRequestDto(request2)).thenReturn(dto2);
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRepository.findByRequestId(2L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findByRequesterIdOrderByCreatedDesc(userId);
        verify(itemRequestMapper, times(2)).toItemRequestDto(any());
    }

    @Test
    void getByUserId_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getByUserId(userId));

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllExceptUser_ShouldReturnOtherUsersRequests() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        User otherUser = createTestUser(2L);
        ItemRequest otherRequest = createTestItemRequest(3L, otherUser);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(3L);
        dto.setDescription("Other user request");
        dto.setRequestorId(2L);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(otherRequest));
        when(itemRequestMapper.toItemRequestDto(otherRequest)).thenReturn(dto);
        when(itemRepository.findByRequestId(3L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllExceptUser(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(0).getRequestorId());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findByRequesterIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void getAllExceptUser_ShouldApplyPagination() {
        Long userId = 1L;
        int from = 1;
        int size = 2;

        User otherUser = createTestUser(2L);
        ItemRequest request1 = createTestItemRequest(1L, otherUser);
        ItemRequest request2 = createTestItemRequest(2L, otherUser);
        ItemRequest request3 = createTestItemRequest(3L, otherUser);

        List<ItemRequest> allRequests = List.of(request1, request2, request3);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(allRequests);
        when(itemRequestMapper.toItemRequestDto(request2)).thenReturn(new ItemRequestDto());
        when(itemRequestMapper.toItemRequestDto(request3)).thenReturn(new ItemRequestDto());
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllExceptUser(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(itemRequestMapper, times(2)).toItemRequestDto(any());
    }

    @Test
    void getAllExceptUser_ShouldReturnEmptyList_WhenNoOtherRequests() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllExceptUser(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRequestMapper, never()).toItemRequestDto(any());
    }

    @Test
    void update_ShouldUpdateItemRequest() {
        Long requestId = 1L;
        User requestor = createTestUser(1L);

        ItemRequest existingRequest = createTestItemRequest(requestId, requestor);
        existingRequest.setDescription("Original description");

        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        ItemRequest updatedRequest = createTestItemRequest(requestId, requestor);
        updatedRequest.setDescription("Updated description");

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(requestId);
        expectedDto.setDescription("Updated description");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(itemRequestRepository.save(existingRequest)).thenReturn(updatedRequest);
        when(itemRequestMapper.toItemRequestDto(updatedRequest)).thenReturn(expectedDto);

        ItemRequestDto result = itemRequestService.update(requestId, updateDto);

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestRepository).save(existingRequest);
        verify(itemRequestMapper).toItemRequestDto(updatedRequest);
    }

    @Test
    void update_ShouldThrowException_WhenRequestNotFound() {
        Long requestId = 999L;
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.update(requestId, updateDto));

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void update_ShouldNotChangeOtherFields_WhenOnlyDescriptionProvided() {
        Long requestId = 1L;
        User requestor = createTestUser(1L);
        LocalDateTime originalCreated = LocalDateTime.now().minusDays(2);

        ItemRequest existingRequest = createTestItemRequest(requestId, requestor);
        existingRequest.setDescription("Original description");
        existingRequest.setCreated(originalCreated);

        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(itemRequestRepository.save(existingRequest)).thenReturn(existingRequest);
        when(itemRequestMapper.toItemRequestDto(existingRequest)).thenReturn(new ItemRequestDto());

        itemRequestService.update(requestId, updateDto);

        assertEquals("Updated description", existingRequest.getDescription());
        assertEquals(originalCreated, existingRequest.getCreated());
        assertEquals(requestor, existingRequest.getRequestor());

        verify(itemRequestRepository).save(existingRequest);
    }

    @Test
    void delete_ShouldDeleteItemRequest() {
        Long requestId = 1L;

        doNothing().when(itemRequestRepository).deleteById(requestId);

        itemRequestService.delete(requestId);

        verify(itemRequestRepository).deleteById(requestId);
    }

    @Test
    void getByUserId_ShouldHandleEmptyResult() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRequestMapper, never()).toItemRequestDto(any());
    }

    @Test
    void getAllExceptUser_ShouldHandleEmptyResultWithPagination() {
        Long userId = 1L;
        int from = 5;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllExceptUser(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}