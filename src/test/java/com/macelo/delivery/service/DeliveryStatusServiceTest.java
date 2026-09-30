package com.macelo.delivery.service;

import com.macelo.delivery.entity.Order;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.enums.DeliveryStatus;
import com.macelo.delivery.enums.UserRole;
import com.macelo.delivery.exception.InvalidStatusTransitionException;

class DeliveryStatusServiceTest {

    private final DeliveryStatusService service = new DeliveryStatusService();
    private Order order;
    private User admin;
    private User operador;
    private User motorista;

    @BeforeEach
    void setUp() {
        order = new Order();
        admin = User.builder().role(UserRole.ADMIN).email("admin@teste.com").build();
        operador = User.builder().role(UserRole.OPERATOR).email("operador@teste.com").build();
        motorista = User.builder().role(UserRole.MOTORISTA).email("motorista@teste.com").build();
    }

    @Test
    void deveAceitarTransicaoValidaCreatedParaInSeparation() {
        order.setStatus(DeliveryStatus.CREATED);

        assertThatCode(() -> service.validateTransition(order, DeliveryStatus.IN_SEPARATION, operador))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarPularEtapas() {
        order.setStatus(DeliveryStatus.CREATED);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.IN_TRANSIT, operador))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("CREATED")
                .hasMessageContaining("IN_TRANSIT");
    }

    @Test
    void deveRejeitarRetrocederDeDeliveredParaInTransit() {
        order.setStatus(DeliveryStatus.DELIVERED);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.IN_TRANSIT, admin))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void naoDevePermitirNenhumaTransicaoAPartirDeDelivered() {
        order.setStatus(DeliveryStatus.DELIVERED);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.CANCELLED, admin))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void naoDevePermitirNenhumaTransicaoAPartirDeCancelled() {
        order.setStatus(DeliveryStatus.CANCELLED);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.CREATED, admin))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void adminPodeCancelarAPartirDeQualquerEstadoNaoTerminal() {
        order.setStatus(DeliveryStatus.IN_TRANSIT);

        assertThatCode(() -> service.validateTransition(order, DeliveryStatus.CANCELLED, admin))
                .doesNotThrowAnyException();
    }

    @Test
    void operadorNaoPodeCancelar() {
        order.setStatus(DeliveryStatus.IN_TRANSIT);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.CANCELLED, operador))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("ADMIN");
    }

    @Test
    void motoristaPodeAvancarDeInTransitParaOutForDelivery() {
        order.setStatus(DeliveryStatus.IN_TRANSIT);

        assertThatCode(() -> service.validateTransition(order, DeliveryStatus.OUT_FOR_DELIVERY, motorista))
                .doesNotThrowAnyException();
    }

    @Test
    void motoristaPodeConcluirDeOutForDeliveryParaDelivered() {
        order.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

        assertThatCode(() -> service.validateTransition(order, DeliveryStatus.DELIVERED, motorista))
                .doesNotThrowAnyException();
    }

    @Test
    void motoristaNaoPodeMexerEmEtapaDeArmazem() {
        order.setStatus(DeliveryStatus.CREATED);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.IN_SEPARATION, motorista))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Motorista");
    }

    @ParameterizedTest
    @EnumSource(value = DeliveryStatus.class, names = {"DELIVERED", "CANCELLED"})
    void estadosTerminaisNuncaTemTransicaoValida(DeliveryStatus estadoTerminal) {
        order.setStatus(estadoTerminal);

        assertThatThrownBy(() -> service.validateTransition(order, DeliveryStatus.CREATED, admin))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}
