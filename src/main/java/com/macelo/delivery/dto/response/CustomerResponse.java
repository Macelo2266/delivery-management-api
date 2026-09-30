package com.macelo.delivery.dto.response;

import com.macelo.delivery.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocument(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt()
        );
    }
}
