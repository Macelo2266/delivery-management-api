package com.macelo.delivery.controller;

import com.macelo.delivery.dto.request.DriverAssignmentRequest;
import com.macelo.delivery.dto.request.OrderFilter;
import com.macelo.delivery.dto.request.OrderRequest;
import com.macelo.delivery.dto.request.StatusUpdateRequest;
import com.macelo.delivery.dto.response.DeliveryStatusHistoryResponse;
import com.macelo.delivery.dto.response.OrderResponse;
import com.macelo.delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação, consulta, atualização de status e histórico de entregas")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Cria um novo pedido",
            description = "Cria o pedido com status inicial CREATED e gera um tracking code único automaticamente. "
                    + "Restrito a ADMIN e OPERADOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (endereço, peso, etc.)"),
            @ApiResponse(responseCode = "404", description = "Cliente informado não existe"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (ex: CLIENTE tentando criar)")
    })
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<Page<OrderResponse>> findAll(
            OrderFilter filter, Pageable pageable, Authentication authentication) {
        return ResponseEntity.ok(orderService.findAll(filter, pageable, authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR') " +
            "or @orderSecurity.isOwner(#id, authentication) " +
            "or @orderSecurity.isAssignedDriver(#id, authentication)")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR') or @orderSecurity.isAssignedDriver(#id, authentication)")
    @Operation(summary = "Altera o status de um pedido",
            description = "Aplica a máquina de estados definida para entregas. ADMIN pode cancelar a partir de "
                    + "qualquer estado não-terminal; OPERADOR avança o fluxo normal mas não cancela; MOTORISTA só "
                    + "pode mover para OUT_FOR_DELIVERY ou DELIVERED, e apenas em pedidos atribuídos a ele. "
                    + "Toda alteração gera um registro no histórico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Transição de status inválida para o estado atual ou papel do usuário"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.changeStatus(id, request, authentication));
    }

    @PatchMapping("/{id}/driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<OrderResponse> assignDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverAssignmentRequest request) {
        return ResponseEntity.ok(orderService.assignDriver(id, request));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR') " +
            "or @orderSecurity.isOwner(#id, authentication) " +
            "or @orderSecurity.isAssignedDriver(#id, authentication)")
    public ResponseEntity<List<DeliveryStatusHistoryResponse>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getHistory(id));
    }

}
