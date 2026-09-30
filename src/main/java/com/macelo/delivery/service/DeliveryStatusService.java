package com.macelo.delivery.service;

import com.macelo.delivery.entity.Order;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.enums.DeliveryStatus;
import com.macelo.delivery.enums.UserRole;
import com.macelo.delivery.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class DeliveryStatusService {

    // grafo de transições válidas — independe de quem está pedindo
    private static final Map<DeliveryStatus, Set<DeliveryStatus>> VALID_TRANSITIONS = new EnumMap<>(DeliveryStatus.class);

    static {
        VALID_TRANSITIONS.put(DeliveryStatus.CREATED,
                EnumSet.of(DeliveryStatus.IN_SEPARATION, DeliveryStatus.CANCELLED));
        VALID_TRANSITIONS.put(DeliveryStatus.IN_SEPARATION,
                EnumSet.of(DeliveryStatus.READY_FOR_DELIVERY, DeliveryStatus.CANCELLED));
        VALID_TRANSITIONS.put(DeliveryStatus.READY_FOR_DELIVERY,
                EnumSet.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.CANCELLED));
        VALID_TRANSITIONS.put(DeliveryStatus.IN_TRANSIT,
                EnumSet.of(DeliveryStatus.OUT_FOR_DELIVERY, DeliveryStatus.CANCELLED));
        VALID_TRANSITIONS.put(DeliveryStatus.OUT_FOR_DELIVERY,
                EnumSet.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED));
        VALID_TRANSITIONS.put(DeliveryStatus.DELIVERED, EnumSet.noneOf(DeliveryStatus.class));
        VALID_TRANSITIONS.put(DeliveryStatus.CANCELLED, EnumSet.noneOf(DeliveryStatus.class));
    }

    // transições que o MOTORISTA tem permissão de executar, mesmo sendo o dono da entrega
    private static final Set<DeliveryStatus> DRIVER_ALLOWED_TARGETS =
            EnumSet.of(DeliveryStatus.OUT_FOR_DELIVERY, DeliveryStatus.DELIVERED);

    public void validateTransition(Order order, DeliveryStatus newStatus, User requestingUser) {
        DeliveryStatus current = order.getStatus();

        Set<DeliveryStatus> allowedNext = VALID_TRANSITIONS.get(current);
        if (allowedNext == null || !allowedNext.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Transição inválida: %s → %s".formatted(current, newStatus));
        }

        if (newStatus == DeliveryStatus.CANCELLED && requestingUser.getRole() != UserRole.ADMIN) {
            throw new InvalidStatusTransitionException("Apenas ADMIN pode cancelar um pedido");
        }

        if (requestingUser.getRole() == UserRole.MOTORISTA
                && !DRIVER_ALLOWED_TARGETS.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Motorista só pode alterar o status para OUT_FOR_DELIVERY ou DELIVERED");
        }
    }
}
