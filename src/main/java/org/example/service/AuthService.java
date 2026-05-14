package org.example.service;

import org.example.dto.AuthResponse;
import org.example.dto.LoginRequest;
import org.example.dto.RefreshRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public void register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(request.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(token, refreshToken.getToken());
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken newRefreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = newRefreshToken.getUser();

        String newToken = jwtService.generateToken(user.getEmail());

        return new AuthResponse(newToken, newRefreshToken.getToken());
    }

    public void logout(RefreshRequest request) {
        RefreshToken newRefreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        refreshTokenService.deleteByUser(newRefreshToken.getUser());
    }

}
