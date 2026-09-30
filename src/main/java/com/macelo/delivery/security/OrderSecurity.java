package com.macelo.delivery.security;

import com.macelo.delivery.entity.Order;
import com.macelo.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean isOwner(Long orderId, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return false;
        }
        Long customerId = userDetails.getUser().getCustomerId();
        if (customerId == null) {
            return false;
        }
        return orderRepository.findById(orderId)
                .map(order -> order.getCustomer().getId().equals(customerId))
                .orElse(false);
    }

    public boolean isAssignedDriver(Long orderId, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return false;
        }
        Long driverId = userDetails.getUser().getDriverId();
        if (driverId == null) {
            return false;
        }
        Order order = orderRepository.findById(orderId).orElse(null);
        return order != null && order.getDriver() != null && order.getDriver().getId().equals(driverId);
    }
}