package ru.practicum.shareit.gt.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.gt.exception.ConflictException;
import ru.practicum.shareit.gt.exception.NotFoundException;
import ru.practicum.shareit.gt.user.dto.UserDto;
import ru.practicum.shareit.gt.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_ShouldCreateUser_WhenValidData() {
        UserDto userDto = new UserDto(null, "User", "user@email.com");
        User user = new User(null, "User", "user@email.com");
        User savedUser = new User(1L, "User", "user@email.com");
        UserDto savedUserDto = new UserDto(1L, "User", "user@email.com");

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(savedUserDto);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() {
        UserDto userDto = new UserDto(null, "User", "existing@email.com");

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void update_ShouldUpdateUser_WhenValidData() {
        Long userId = 1L;
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated@email.com");

        User existingUser = new User(userId, "Original Name", "original@email.com");
        User updatedUser = new User(userId, "Updated Name", "updated@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(updateDto);

        UserDto result = userService.update(userId, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@email.com", result.getEmail());
    }

    @Test
    void update_ShouldThrowException_WhenEmailAlreadyUsed() {
        Long userId = 1L;
        UserDto updateDto = new UserDto(userId, "User", "existing@email.com");

        User existingUser = new User(userId, "User", "original@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(userId, updateDto));
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        Long userId = 1L;
        User user = new User(userId, "User", "user@email.com");
        UserDto userDto = new UserDto(userId, "User", "user@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }
}