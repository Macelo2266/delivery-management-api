package com.macelo.delivery.service;
import com.macelo.delivery.dto.request.RegisterRequest;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.enums.UserRole;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.repository.UserRepository;
import com.macelo.delivery.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.springframework.security.authorization.ConditionalAuthorizationManager.when;
import static sun.java2d.cmm.ProfileDataVerifier.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private com.macelo.delivery.security.JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Novo Usuário");
        registerRequest.setEmail("novo@teste.com");
        registerRequest.setPassword("senha1234");
        registerRequest.setRole(UserRole.OPERADOR);
    }

    @Test
    void deveRegistrarUsuarioComSenhaCriptografada() {
        when(userRepository.existsByEmail("novo@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("hash-simulado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        var response = authService.register(registerRequest);

        assertThat(response.getEmail()).isEqualTo("novo@teste.com");
        assertThat(response.getRole()).isEqualTo(UserRole.OPERADOR);
        verify(passwordEncoder).encode("senha1234");
        verify(userRepository).save(argThat(user -> user.getPassword().equals("hash-simulado")));
    }

    @Test
    void deveRejeitarRegistroComEmailJaExistente() {
        when(userRepository.existsByEmail("novo@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("novo@teste.com");

        verify(userRepository, never()).save(any());
    }
}
