# Sistema de Chamados (CHAMADOS-DAS)

API REST para gestão de chamados de suporte, feita com Spring Boot 4, Java 21, Spring Data JPA, Spring Security e banco H2 (padrão) ou PostgreSQL.

## Como rodar

Com H2 em memória (padrão):

```
.\mvnw spring-boot:run
```

Com PostgreSQL (profile `postgres`):

1. Instale o PostgreSQL e crie o banco `chamadosdb`.
2. Rode:

```
$env:DB_PASSWORD="sua_senha"
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

A senha vem da variável de ambiente `DB_PASSWORD` (usuário padrão `postgres`, ajustável por `DB_USER`).

A aplicação sobe em `http://localhost:8080`. Ao iniciar, são carregados dados de demonstração (só se o banco estiver vazio).

Para rodar os testes:

```
.\mvnw test
```

## Segurança

Autenticação HTTP Basic com usuários em memória:

| Usuário | Senha | Perfil | Acesso |
|---|---|---|---|
| admin | admin123 | ADMIN | Total |
| tecnico | tecnico123 | TECNICO | Clientes, chamados, comentários, dashboard e leitura de categorias. Sem DELETE, sem /tecnicos e sem escrita em /categorias |

Swagger e console do H2 são liberados sem login. Exemplo:

```
curl.exe -u admin:admin123 http://localhost:8080/clientes
```

Sem credenciais a API responde 401. Sem permissão, 403.

## Links úteis

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Console do H2 | http://localhost:8080/h2-console |
| Dashboard | http://localhost:8080/dashboard |

Console do H2: JDBC URL `jdbc:h2:mem:chamadosdb`, usuário `sa`, senha em branco.

## Endpoints principais

- `/clientes`, `/categorias`, `/tecnicos`: CRUD
- `/chamados`: CRUD, mais `PATCH /chamados/{id}/atribuir` e `PATCH /chamados/{id}/status`
- `POST /chamados/{id}/comentarios` e `GET /chamados/{id}/comentarios`
- `GET /clientes/{id}/chamados`
- `GET /dashboard`: total de chamados e contagem por status e por prioridade

## Exemplos de consulta de chamados

| Objetivo | Requisição |
|---|---|
| Listar todos (10 por página, mais novos primeiro) | `GET /chamados` |
| Filtrar por status | `GET /chamados?status=ABERTO` |
| Filtrar por prioridade | `GET /chamados?prioridade=ALTA` |
| Filtrar por cliente | `GET /chamados?clienteId=1` |
| Combinar filtros | `GET /chamados?status=ABERTO&prioridade=ALTA&clienteId=1` |
| Paginar | `GET /chamados?page=0&size=5` |
| Ordenar | `GET /chamados?sort=dataAbertura,asc` |
| Chamados de um cliente | `GET /clientes/1/chamados` |

Status: `ABERTO`, `EM_ATENDIMENTO`, `AGUARDANDO_CLIENTE`, `FECHADO`.
Prioridades: `BAIXA`, `MEDIA`, `ALTA`, `CRITICA`.

## Planejamento do front-end

| Tela | Endpoints consumidos |
|---|---|
| Login | HTTP Basic (usuário e senha enviados no cabeçalho Authorization) |
| Dashboard | `GET /dashboard` |
| Lista de clientes | `GET /clientes`, `POST /clientes`, `PUT /clientes/{id}`, `DELETE /clientes/{id}` |
| Lista de categorias | `GET /categorias`, `POST /categorias`, `PUT /categorias/{id}`, `DELETE /categorias/{id}` |
| Lista de técnicos | `GET /tecnicos`, `POST /tecnicos`, `PUT /tecnicos/{id}`, `DELETE /tecnicos/{id}` |
| Lista de chamados (filtros e paginação) | `GET /chamados?status=&prioridade=&clienteId=&page=&size=` |
| Novo chamado | `POST /chamados`, `GET /clientes`, `GET /categorias` |
| Detalhe do chamado | `GET /chamados/{id}`, `PATCH /chamados/{id}/status`, `PATCH /chamados/{id}/atribuir`, `GET /chamados/{id}/comentarios`, `POST /chamados/{id}/comentarios` |
| Chamados de um cliente | `GET /clientes/{id}/chamados` |

## Itens opcionais do 13.8

Implementados: Spring Security, dashboard por status e prioridade, documentação OpenAPI/Swagger, migração para PostgreSQL com profiles, dados iniciais para demonstração e planejamento do front-end.
