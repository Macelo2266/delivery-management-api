package com.macelo.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macelo.delivery.config.TestContainersConfig;
import com.macelo.delivery.dto.request.AddressRequest;
import com.macelo.delivery.dto.request.OrderRequest;
import com.macelo.delivery.entity.Customer;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.enums.UserRole;
import com.macelo.delivery.repository.CustomerRepository;
import com.macelo.delivery.repository.UserRepository;
import com.macelo.delivery.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.web.servlet.function.ServerResponse.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String operadorToken;
    private String clienteToken;
    private Long customerId;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.save(Customer.builder()
                .name("Cliente IT").document("11122233344")
                .email("cliente-it@teste.com").phone("81999998888").build());
        customerId = customer.getId();

        User operador = userRepository.save(User.builder()
                .name("Operador IT").email("operador-it@teste.com")
                .password(passwordEncoder.encode("senha1234"))
                .role(UserRole.OPERADOR).active(true).build());
        operadorToken = jwtTokenProvider.generateToken(operador.getEmail(), operador.getRole().name());

        User clienteUser = userRepository.save(User.builder()
                .name("Cliente User IT").email("clienteuser-it@teste.com")
                .password(passwordEncoder.encode("senha1234"))
                .role(UserRole.CLIENTE).active(true).customerId(customerId).build());
        clienteToken = jwtTokenProvider.generateToken(clienteUser.getEmail(), clienteUser.getRole().name());
    }

    private OrderRequest buildOrderRequest() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(customerId);
        request.setDescription("Pacote de teste de integração");
        request.setWeight(java.math.BigDecimal.valueOf(1.2));

        AddressRequest pickup = new AddressRequest();
        pickup.setStreet("Rua Teste"); pickup.setNumber("10"); pickup.setNeighborhood("Centro");
        pickup.setCity("Recife"); pickup.setState("PE"); pickup.setZipCode("50000000");
        AddressRequest delivery = new AddressRequest();
        delivery.setStreet("Rua Destino"); delivery.setNumber("20"); delivery.setNeighborhood("Boa Vista");
        delivery.setCity("Recife"); delivery.setState("PE"); delivery.setZipCode("50100000");

        request.setPickupAddress(pickup);
        request.setDeliveryAddress(delivery);
        return request;
    }

    @Test
    void operadorDeveCriarPedidoComSucesso() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + operadorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.trackingCode").isNotEmpty());
    }

    @Test
    void clienteSoDeveVerOsPropriosPedidos() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void requisicaoSemTokenDeveSerRejeitada() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteSoDeveVerOsPropriosPedidos() throws Exception {
        // cria um pedido para o cliente do setup
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + operadorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customer.id").value(customerId));
    }
}

