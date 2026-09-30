# Delivery Management API

API REST completa para gerenciamento de operações de uma empresa de entregas/logística — clientes, motoristas, veículos, pedidos, endereços e histórico de entregas, com autenticação JWT, controle de acesso por papéis, testes automatizados e execução via Docker.

Projeto de portfólio construído para demonstrar conhecimentos práticos de desenvolvimento Backend Java em um cenário próximo do real, com regras de negócio, camadas bem definidas e boas práticas de engenharia.

## Funcionalidades

- Autenticação via JWT (registro e login)
- Controle de acesso por papéis: `ADMIN`, `OPERADOR`, `MOTORISTA`, `CLIENTE`
- CRUD completo de clientes, motoristas, veículos e usuários
- Criação de pedidos com endereços de retirada e entrega embutidos
- Máquina de estados para o ciclo de vida de uma entrega, com transições validadas por regra de negócio e por papel do usuário
- Atribuição de motorista a pedidos
- Histórico completo e auditável de mudanças de status por pedido
- Filtros combináveis e paginação na listagem de pedidos (status, tracking code, cliente, motorista, data de criação)
- Tratamento global de exceções com respostas HTTP padronizadas
- Documentação interativa via Swagger/OpenAPI
- Testes unitários (regras de negócio) e de integração (fluxo HTTP completo, com banco real via Testcontainers)
- Execução completa via Docker Compose
- CI configurado com GitHub Actions

## Tecnologias

- Java 21 (LTS)
- Spring Boot 3.x (Web, Data JPA, Security, Validation)
- JWT (biblioteca `jjwt`)
- PostgreSQL
- Flyway (versionamento de banco)
- Lombok
- JUnit 5, Mockito, MockMvc, Testcontainers
- Swagger / OpenAPI (springdoc)
- Maven
- Docker e Docker Compose
- GitHub Actions

## Arquitetura

O projeto segue uma arquitetura em camadas por responsabilidade:

```
Controller → Service → Repository → PostgreSQL
```

- **Controller**: recebe a requisição HTTP, valida o formato de entrada (via DTO + Bean Validation) e delega para o Service. Não contém regra de negócio.
- **Service**: concentra toda a lógica de negócio — validações contextuais, máquina de estados, orquestração entre repositórios.
- **Repository**: acesso a dados via Spring Data JPA, incluindo `Specification` para filtros dinâmicos.
- **DTO**: entidades JPA nunca são expostas diretamente pelos endpoints. Requests e Responses são objetos próprios, desacoplados do modelo de persistência.
- **Mapper**: conversão entre entidade e DTO, isolando essa lógica de tradução.
- **Security**: configuração do Spring Security, filtro JWT, e beans de autorização contextual (ex.: verificar se o usuário autenticado é o dono do recurso).
- **Exception**: exceções de domínio e tratamento global centralizado via `@RestControllerAdvice`.

Estrutura de pacotes:

```
src/main/java/com/macelo/delivery
├── controller
├── service
├── repository
│   └── specification
├── entity
├── dto
│   ├── request
│   └── response
├── mapper
├── exception
├── security
├── config
└── enums
```

## Modelagem

```
Customer 1 ──── N Order
Driver   1 ──── N Order
Order    1 ──── N DeliveryStatusHistory
Order    N ──── 1 Address (pickup)
Order    N ──── 1 Address (delivery)

User (autenticação) ── vínculo opcional ──> Customer  (quando role = CLIENTE)
User (autenticação) ── vínculo opcional ──> Driver    (quando role = MOTORISTA)
```

`User` é responsável apenas pela autenticação (email, senha, papel). `Customer` e `Driver` são entidades de negócio independentes, vinculadas a um `User` por uma FK opcional — decisão que evita herança JPA complexa para um ganho de modelagem marginal.

Máquina de estados da entrega:

```
CREATED → IN_SEPARATION → READY_FOR_DELIVERY → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
```

Qualquer estado não-terminal pode ir para `CANCELLED` (exclusivo de `ADMIN`). `DELIVERED` e `CANCELLED` são estados terminais, sem transição de saída.

## Autenticação

A API usa **JWT** (JSON Web Token) para autenticação stateless — nenhuma sessão é mantida no servidor.

Fluxo:

1. `POST /api/v1/auth/register` cria o usuário (senha armazenada com hash BCrypt).
2. `POST /api/v1/auth/login` valida as credenciais e retorna um token.
3. O token é enviado em todas as requisições subsequentes no header `Authorization: Bearer <token>`.
4. Um filtro (`JwtAuthenticationFilter`) valida o token em cada requisição e popula o contexto de segurança, permitindo que `@PreAuthorize` restrinja endpoints por papel (`ADMIN`, `OPERADOR`, `MOTORISTA`, `CLIENTE`) e, em alguns casos, por posse do recurso (ex.: um motorista só acessa suas próprias entregas).

A senha nunca é retornada em nenhuma resposta da API — os DTOs de resposta simplesmente não possuem esse campo.

## Como executar

Pré-requisitos: Docker e Docker Compose instalados.

```bash
git clone <url-do-repositorio>
cd delivery-management-api
cp .env.example .env
# edite o .env e defina um JWT_SECRET forte (ex: openssl rand -base64 32)
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.

Para desenvolvimento local sem Docker na API (só o banco em container):

```bash
docker compose up db -d
./mvnw spring-boot:run
```

## Variáveis de ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5432` |
| `DB_NAME` | Nome do banco | `delivery_db` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `JWT_SECRET` | Chave usada para assinar os tokens JWT (mínimo 256 bits) | — |
| `JWT_EXPIRATION` | Tempo de expiração do token em milissegundos | `86400000` (24h) |
| `SERVER_PORT` | Porta em que a API roda | `8080` |

Nunca comite valores reais de `JWT_SECRET` ou credenciais de banco — use sempre o `.env`, que está no `.gitignore`.

## Swagger

Com a aplicação em execução, a documentação interativa está disponível em:

```
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos direto pela interface: faça login via `POST /api/v1/auth/login`, copie o token retornado, clique em **Authorize** no topo da página e cole o token (sem o prefixo `Bearer`).

O JSON OpenAPI puro fica disponível em `http://localhost:8080/api-docs`.

## Testes

```bash
./mvnw test
```

A suíte combina:

- **Testes unitários** (Service + Mockito) — validam regras de negócio isoladamente, sem Spring context e sem banco. Cobrem principalmente a máquina de estados de entrega, geração de tracking code e regras de criação/edição de pedidos.
- **Testes de integração** (`@SpringBootTest` + `MockMvc` + Testcontainers) — sobem um PostgreSQL real em container Docker e testam o fluxo HTTP completo, incluindo autenticação, autorização por papel e códigos de erro.

**Pré-requisito**: Docker precisa estar em execução localmente (ou no runner de CI) para os testes de integração passarem.

## Exemplos da API

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@teste.com","password":"senha1234"}'
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

### Criação de cliente

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","document":"12345678900","email":"joao@email.com","phone":"81999999999"}'
```

### Criação de pedido

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "pickupAddress": {"street":"Rua A","number":"100","neighborhood":"Centro","city":"Recife","state":"PE","zipCode":"50000000"},
    "deliveryAddress": {"street":"Rua B","number":"200","neighborhood":"Boa Vista","city":"Recife","state":"PE","zipCode":"50100000"},
    "description":"Caixa de eletrônicos",
    "weight": 3.5
  }'
```

### Alteração de status

```bash
curl -X PATCH http://localhost:8080/api/v1/orders/1/status \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_SEPARATION", "description": "Separando itens no galpão"}'
```

### Consulta de pedido com filtros

```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/orders?status=IN_TRANSIT&page=0&size=10"
```

## Decisões técnicas

- **`User` separado de `Customer`/`Driver`, sem herança JPA**: `User` cuida só de autenticação; o vínculo com o cadastro de negócio é uma FK opcional (`customerId`/`driverId`), resolvida na camada de aplicação. Evita a complexidade de mapeamento de herança para um ganho de modelagem marginal.
- **`Address` imutável, sem endpoint próprio**: um endereço só existe no contexto de um pedido. Editar um pedido gera novos registros de endereço em vez de alterar os existentes.
- **`FetchType.LAZY` em todos os relacionamentos `@ManyToOne`, com `open-in-view: false`**: previne consultas N+1 acidentais e força a resolução explícita dos relacionamentos na camada de Service, dentro do contexto transacional correto.
- **Máquina de estados centralizada em `DeliveryStatusService`**: separa a validação de "isso é uma transição fisicamente válida?" da validação de "esse papel de usuário pode pedir essa transição?", mantendo cada regra testável isoladamente.
- **Autorização contextual via beans dedicados (`DriverSecurity`, `OrderSecurity`) usados em `@PreAuthorize`**: permite expressar regras como "só o próprio motorista ou o dono do pedido" sem misturar lógica de autorização com lógica de negócio no Service.
- **Filtros dinâmicos via JPA Specifications**: evita explosão combinatória de métodos de repositório e mantém os índices de banco eficazes, em vez de uma única query JPQL com múltiplas condições opcionais.
- **Testcontainers em vez de H2 nos testes de integração**: garante que os testes rodam contra o mesmo banco (PostgreSQL) usado em produção, evitando divergências de comportamento entre bancos.
- **Build multi-stage no Dockerfile, com usuário não-root**: imagem final mínima (JRE Alpine) e sem privilégios administrativos no processo em execução.

## Melhorias futuras

- Integração com serviço de mapas para cálculo de rota e distância entre endereços
- Notificações (email/push) em mudanças de status do pedido
- Rastreamento em tempo real via WebSocket
- Cache com Redis para consultas frequentes (ex.: listagem de motoristas ativos)
- Mensageria assíncrona (ex.: RabbitMQ/Kafka) para desacoplar a geração de histórico e notificações da requisição principal
- Métricas e observabilidade (Actuator + Prometheus/Grafana)
- Deploy em ambiente cloud (ex.: AWS, Railway, Render)
- Vínculo formal entre veículo, motorista e pedido
