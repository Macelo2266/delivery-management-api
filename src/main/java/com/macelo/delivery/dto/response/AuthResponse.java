package com.macelo.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private long expiresIn;

    public static AuthResponse of(String token, long expiresIn) {
        return new AuthResponse(token, "Bearer", expiresIn);
    }
}
