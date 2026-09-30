package com.macelo.delivery.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> fields;

    public ErrorResponse(int status, String error, String message, Map<String, String> fields) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.fields = fields;
    }

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, Map.of());
    }
}
