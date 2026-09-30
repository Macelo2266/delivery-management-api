package com.macelo.delivery.service;

import com.macelo.delivery.dto.request.AddressRequest;
import com.macelo.delivery.dto.request.OrderRequest;
import com.macelo.delivery.entity.Address;
import com.macelo.delivery.entity.Customer;
import com.macelo.delivery.entity.Order;
import com.macelo.delivery.enums.DeliveryStatus;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.AddressMapper;
import com.macelo.delivery.mapper.OrderMapper;
import com.macelo.delivery.repository.AddressRepository;
import com.macelo.delivery.repository.CustomerRepository;
import com.macelo.delivery.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static sun.java2d.cmm.ProfileDataVerifier.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private AddressMapper addressMapper;
    @Mock private com.macelo.delivery.repository.DeliveryStatusHistoryRepository historyRepository;
    @Mock
    private com.macelo.delivery.repository.DriverRepository driverRepository;
    @Mock private DeliveryStatusService deliveryStatusService;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest request;

    @BeforeEach
    void setUp() {
        request = new OrderRequest();
        request.setCustomerId(1L);
        request.setDescription("Pacote de teste");
        request.setWeight(BigDecimal.valueOf(2.5));

        AddressRequest pickup = new AddressRequest();
        pickup.setStreet("Rua A"); pickup.setNumber("1"); pickup.setNeighborhood("Centro");
        pickup.setCity("Recife"); pickup.setState("PE"); pickup.setZipCode("50000000");
        AddressRequest delivery = new AddressRequest();
        delivery.setStreet("Rua B"); delivery.setNumber("2"); delivery.setNeighborhood("Boa Vista");
        delivery.setCity("Recife"); delivery.setState("PE"); delivery.setZipCode("50100000");

        request.setPickupAddress(pickup);
        request.setDeliveryAddress(delivery);
    }

    @Test
    void deveRejeitarCriacaoComClienteInexistente() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void deveGerarTrackingCodeUnicoQuandoHaColisao() {
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));
        // simula que o primeiro código gerado já existe, forçando o loop a gerar outro
        when(orderRepository.existsByTrackingCode(any())).thenReturn(true, false);
        when(orderMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(new Order());
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.create(request);

        // confirma que o repositório foi consultado mais de uma vez até achar um código livre
        verify(orderRepository, atLeast(2)).existsByTrackingCode(any());
    }

    @Test
    void naoDevePermitirEditarPedidoForaDoStatusCreated() {
        Order existing = new Order();
        existing.setStatus(DeliveryStatus.IN_TRANSIT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> orderService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CREATED");

        verify(customerRepository, never()).findById(any());
    }
}
