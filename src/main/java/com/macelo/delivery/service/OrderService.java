package com.macelo.delivery.service;


import com.macelo.delivery.dto.request.DriverAssignmentRequest;
import com.macelo.delivery.dto.request.OrderFilter;
import com.macelo.delivery.dto.request.OrderRequest;
import com.macelo.delivery.dto.request.StatusUpdateRequest;
import com.macelo.delivery.dto.response.DeliveryStatusHistoryResponse;
import com.macelo.delivery.dto.response.OrderResponse;
import com.macelo.delivery.entity.*;
import com.macelo.delivery.enums.DeliveryStatus;
import com.macelo.delivery.enums.UserRole;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.AddressMapper;
import com.macelo.delivery.mapper.OrderMapper;
import com.macelo.delivery.repository.*;
import com.macelo.delivery.repository.specification.OrderSpecification;
import com.macelo.delivery.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String TRACKING_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final DeliveryStatusHistoryRepository historyRepository;
    private final DriverRepository driverRepository;
    private final DeliveryStatusService deliveryStatusService;

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Customer customer = getCustomerOrThrow(request.getCustomerId());

        Address pickup = addressRepository.save(addressMapper.toEntity(request.getPickupAddress()));
        Address delivery = addressRepository.save(addressMapper.toEntity(request.getDeliveryAddress()));

        String trackingCode = generateUniqueTrackingCode();
        Order order = orderMapper.toEntity(request, trackingCode, customer, pickup, delivery);

        return OrderResponse.from(orderRepository.save(order));
    }

    public Page<OrderResponse> findAll(OrderFilter filter, Pageable pageable, Authentication authentication) {
        User currentUser = extractUser(authentication);

        Specification<Order> spec = OrderSpecification.withFilters(filter);

        if (currentUser.getRole() == UserRole.CLIENTE) {
            if (currentUser.getCustomerId() == null) {
                throw new BusinessException("Usuário CLIENTE sem cliente vinculado");
            }
            // CLIENTE nunca escolhe customerId livremente — é sempre o próprio, sobrescrevendo qualquer valor da query string
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("customer").get("id"), currentUser.getCustomerId()));
        }

        return orderRepository.findAll(spec, pageable).map(OrderResponse::from);
    }
    public OrderResponse findById(Long id) {
        return OrderResponse.from(getOrderOrThrow(id));
    }

    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = getOrderOrThrow(id);

        if (order.getStatus() != DeliveryStatus.CREATED) {
            throw new BusinessException(
                    "Só é possível editar um pedido enquanto o status for CREATED. Status atual: " + order.getStatus());
        }

        Customer customer = getCustomerOrThrow(request.getCustomerId());
        Address pickup = addressRepository.save(addressMapper.toEntity(request.getPickupAddress()));
        Address delivery = addressRepository.save(addressMapper.toEntity(request.getDeliveryAddress()));

        orderMapper.updateMutableFields(order, request, customer, pickup, delivery);
        return OrderResponse.from(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = getOrderOrThrow(id);
        orderRepository.delete(order);
    }
    @Transactional
    public OrderResponse changeStatus(Long id, StatusUpdateRequest request, Authentication authentication) {
        Order order = getOrderOrThrow(id);
        User requestingUser = extractUser(authentication);

        deliveryStatusService.validateTransition(order, request.getStatus(), requestingUser);

        order.setStatus(request.getStatus());
        if (request.getStatus() == DeliveryStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        DeliveryStatusHistory history = DeliveryStatusHistory.builder()
                .order(order)
                .status(request.getStatus())
                .description(request.getDescription())
                .changedBy(requestingUser.getEmail())
                .build();
        historyRepository.save(history);

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse assignDriver(Long id, DriverAssignmentRequest request) {
        Order order = getOrderOrThrow(id);

        if (order.getStatus() == DeliveryStatus.DELIVERED || order.getStatus() == DeliveryStatus.CANCELLED) {
            throw new BusinessException(
                    "Não é possível atribuir motorista a um pedido com status " + order.getStatus());
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado: id " + request.getDriverId()));

        if (!driver.isActive()) {
            throw new BusinessException("Motorista inativo não pode ser atribuído a um pedido");
        }

        order.setDriver(driver);
        return OrderResponse.from(order);
    }

    public List<DeliveryStatusHistoryResponse> getHistory(Long id) {
        getOrderOrThrow(id);
        return historyRepository.findByOrderIdOrderByChangedAtAsc(id).stream()
                .map(DeliveryStatusHistoryResponse::from)
                .toList();
    }

    private Customer getCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: id " + customerId));
    }

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: id " + id));
    }

    private User extractUser(Authentication authentication) {
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }

    private String generateUniqueTrackingCode() {
        String code;
        do {
            code = "DLV" + randomAlphanumeric(9);
        } while (orderRepository.existsByTrackingCode(code));
        return code;
    }

    private String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(TRACKING_CODE_CHARS.charAt(RANDOM.nextInt(TRACKING_CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
