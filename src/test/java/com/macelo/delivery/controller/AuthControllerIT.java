package com.macelo.delivery.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.macelo.delivery.config.TestContainersConfig;
import com.macelo.delivery.dto.request.LoginRequest;
import com.macelo.delivery.dto.request.RegisterRequest;
import com.macelo.delivery.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.web.servlet.function.ServerResponse.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
class AuthControllerIT {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRegistrarELogarComSucesso() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Usuário IT");
        register.setEmail("it-user@teste.com");
        register.setPassword("senha1234");
        register.setRole(UserRole.OPERADOR);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("it-user@teste.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginRequest login = new LoginRequest();
        login.setEmail("it-user@teste.com");
        login.setPassword("senha1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void deveRejeitarRegistroComEmailDuplicado() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Duplicado");
        register.setEmail("duplicado@teste.com");
        register.setPassword("senha1234");
        register.setRole(UserRole.CLIENTE);

        String body = objectMapper.writeValueAsString(register);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Business Rule Violation"));
    }

    @Test
    void deveRejeitarRegistroComDadosInvalidos() throws Exception {
        String bodyInvalido = """
                {"name":"","email":"nao-e-email","password":"123","role":"ADMIN"}
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    void deveRejeitarLoginComCredenciaisInvalidas() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("naoexiste@teste.com");
        login.setPassword("qualquercoisa");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
