package com.macelo.delivery.mapper;

import com.macelo.delivery.dto.request.CustomerRequest;
import com.macelo.delivery.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .name(request.getName())
                .document(request.getDocument())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
    }

    public void updateEntity(Customer customer, CustomerRequest request) {
        customer.setName(request.getName());
        customer.setDocument(request.getDocument());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
    }
}
