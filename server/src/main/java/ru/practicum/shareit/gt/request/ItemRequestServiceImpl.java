package ru.practicum.shareit.gt.request;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.request.dto.ItemRequestDto;
import ru.practicum.shareit.gt.request.model.ItemRequest;
import ru.practicum.shareit.gt.user.UserRepository;
import ru.practicum.shareit.gt.user.model.User;

import ru.practicum.shareit.gt.item.ItemRepository;
import ru.practicum.shareit.gt.item.ItemMapper;
import ru.practicum.shareit.gt.item.dto.ItemDto;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса запросов вещей.
 */
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    // Внедрение зависимости репозитория через конструктор
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    /**
     * Создает запрос, предварительно проверив существование пользователя
     */
    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Создаем Entity запроса с автоматическим заполнением полей
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requester);
        itemRequest.setCreated(java.time.LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        // Возвращаем DTO с пустым списком items (для нового запроса)
        ItemRequestDto resultDto = new ItemRequestDto();
        resultDto.setId(savedRequest.getId());
        resultDto.setDescription(savedRequest.getDescription());
        resultDto.setRequestorId(savedRequest.getRequestor().getId());
        resultDto.setCreated(savedRequest.getCreated());
        resultDto.setItems(java.util.Collections.emptyList());

        return itemRequestMapper.toItemRequestDto(savedRequest);
    }


    /**
     * Находит запрос по ID
     */
    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));

        ItemRequestDto dto = itemRequestMapper.toItemRequestDto(itemRequest);
        // Загружаем вещи, созданные в ответ на этот запрос
        dto.setItems(getItemsForRequest(id));
        return dto;
    }

    /**
     * Возвращает запросы текущего пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    dto.setItems(getItemsForRequest(request.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Возвращает запросы других пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllExceptUser(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId).stream()
                .skip(from)
                .limit(size)
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    dto.setItems(getItemsForRequest(request.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Обновляет запрос
     */
    @Override
    public ItemRequestDto update(Long id, ItemRequestDto itemRequestDto) {
        ItemRequest existingRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));

        if (itemRequestDto.getDescription() != null) {
            existingRequest.setDescription(itemRequestDto.getDescription());
        }

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(existingRequest));
    }

    /**
     * Удаляет запрос.
     *
     * @param id идентификатор запроса для удаления.
     */
    @Override
    public void delete(Long id) {
        itemRequestRepository.deleteById(id);
    }

    /**
     * Вспомогательный метод для получения вещей-ответов на запрос.
     *
     * @param requestId ID запроса, для которого ищем вещи-ответы
     * @return список DTO вещей, созданных в ответ на запрос
     */
    private List<ItemDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
