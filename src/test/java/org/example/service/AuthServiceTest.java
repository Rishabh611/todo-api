package org.example.service;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private  RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Rishabh");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rishabh");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("USER", savedUser.getRole());

        verify(passwordEncoder).encode("password123");

    }

    @Test
    public void shouldLoginSuccessfullyAndReturnToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        User savedUser = new User();
        savedUser.setRole("USER");
        savedUser.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken("token");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

        when(jwtService.generateToken(loginRequest.getEmail())).thenReturn("TOKEN");

        assertEquals("TOKEN", authService.login(loginRequest).getToken());
    }

    @Test
    public void shouldThrowExceptionOnBadCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad Credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        
    }
}
