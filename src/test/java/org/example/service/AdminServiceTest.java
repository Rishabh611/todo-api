package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void shouldDeleteUserSuccessfully() {
        User user =  new User();
        user.setEmail("test@example.com");
        user.setName("test");
        user.setRole("USER");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void shouldThrowExceptionWhenUserIsAdmin() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setName("test");
        user.setRole("ADMIN");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> adminService.deleteUserById(user.getId()));
    }

}
