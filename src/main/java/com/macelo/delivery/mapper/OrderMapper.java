package com.macelo.delivery.mapper;

import com.macelo.delivery.dto.request.OrderRequest;
import com.macelo.delivery.entity.Address;
import com.macelo.delivery.entity.Customer;
import com.macelo.delivery.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequest request, String trackingCode, Customer customer,
                          Address pickupAddress, Address deliveryAddress) {
        return Order.builder()
                .trackingCode(trackingCode)
                .customer(customer)
                .pickupAddress(pickupAddress)
                .deliveryAddress(deliveryAddress)
                .description(request.getDescription())
                .weight(request.getWeight())
                .estimatedDeliveryAt(request.getEstimatedDeliveryAt())
                .build();
        // status é setado no @PrePersist da entidade (Etapa 3) — não precisamos repetir aqui
    }

    public void updateMutableFields(Order order, OrderRequest request, Customer customer,
                                    Address pickupAddress, Address deliveryAddress) {
        order.setCustomer(customer);
        order.setPickupAddress(pickupAddress);
        order.setDeliveryAddress(deliveryAddress);
        order.setDescription(request.getDescription());
        order.setWeight(request.getWeight());
        order.setEstimatedDeliveryAt(request.getEstimatedDeliveryAt());
    }
}
