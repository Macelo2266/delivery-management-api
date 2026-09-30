package com.macelo.delivery.service;

import com.macelo.delivery.dto.request.LoginRequest;
import com.macelo.delivery.dto.request.RegisterRequest;
import com.macelo.delivery.dto.response.AuthResponse;
import com.macelo.delivery.dto.response.UserResponse;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.repository.UserRepository;
import com.macelo.delivery.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, jwtTokenProvider.getExpirationMs());
    }

}
