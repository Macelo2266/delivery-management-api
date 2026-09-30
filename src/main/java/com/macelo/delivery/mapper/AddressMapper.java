package com.macelo.delivery.mapper;

import com.macelo.delivery.dto.request.AddressRequest;
import com.macelo.delivery.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequest request) {
        return Address.builder()
                .street(request.getStreet())
                .number(request.getNumber())
                .complement(request.getComplement())
                .neighborhood(request.getNeighborhood())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(normalizeZipCode(request.getZipCode()))
                .build();
    }

    private String normalizeZipCode(String zipCode) {
        return zipCode.contains("-") ? zipCode : zipCode.substring(0, 5) + "-" + zipCode.substring(5);
    }
}
