# Central de Solicitações

Aplicação Full Stack para registrar solicitações internas e acompanhar sua resolução, desenvolvida para o desafio técnico do processo seletivo 2026.2 da LOADING Desenvolvimento Jr.

Cada solicitação tem título, descrição, categoria e prioridade (baixa, média ou alta) e segue um fluxo de status obrigatório: **Aberta → Em andamento → Concluída**. A API rejeita saltos e retrocessos, e os dados ficam em PostgreSQL.

## Sumário

- [Tecnologias](#tecnologias)
- [Funcionalidades implementadas](#funcionalidades-implementadas)
- [Como executar](#como-executar)
- [Como testar cada funcionalidade](#como-testar-cada-funcionalidade)
- [Testes automatizados](#testes-automatizados)
- [Arquitetura](#arquitetura)
- [Modelo de dados](#modelo-de-dados)
- [API](#api)
- [Configuração e produção](#configuração-e-produção)
- [Decisões de projeto](#decisões-de-projeto)
- [Uso de inteligência artificial](#uso-de-inteligência-artificial)

## Tecnologias

**Backend**

- Java 21 e Spring Boot 3.3
- Spring Web, Spring Validation, Spring Security (JWT stateless)
- Spring Data JPA e PostgreSQL 16, com migrações Flyway
- SpringDoc OpenAPI (Swagger UI)
- JUnit 5, Mockito, AssertJ, MockMvc e H2 (apenas nos testes)

**Frontend**

- React 18, TypeScript e Vite
- Tailwind CSS
- React Router 6
- Axios

**Infraestrutura**

- Docker e Docker Compose (PostgreSQL, API e frontend servido por Nginx)

## Funcionalidades implementadas

**Requisitos obrigatórios**

| Código | Requisito | Onde está |
|---|---|---|
| RF01 | Cadastro com título, descrição, categoria e prioridade; status inicial sempre Aberta | `POST /api/requests`, tela "Nova solicitação" |
| RF02 | Listagem e consulta de dados e status | `GET /api/requests`, `GET /api/requests/{id}`, telas de lista e detalhe |
| RF03 | Filtros por status e por prioridade | parâmetros `status` e `priority`, filtros da tela de lista |
| RF04 | Fluxo Aberta → Em andamento → Concluída, sem saltar etapas | `PATCH /api/requests/{id}/status`, regra em `RequestStatus` |
| RF05 | Persistência após reiniciar | PostgreSQL com volume Docker e migrações Flyway |
| RF06 | Integração e validação com retorno compreensível | Bean Validation, regras de domínio, `GlobalExceptionHandler` e mensagens na interface |

**Opcionais**

- Busca textual (título, descrição e categoria)
- Edição e exclusão de solicitações
- Histórico de mudanças de status
- Autenticação (cadastro e login com JWT)
- Ordenação por data, título, prioridade ou status
- Testes automatizados (unitários e de integração)
- Deploy: Dockerfiles, Docker Compose e configuração por variáveis de ambiente

**Melhorias adicionais**

- Paginação na listagem
- Dashboard com total, abertas, em andamento e concluídas
- Controle de acesso por perfil (`ADMIN` e `USER`)
- Auditoria de operações (usuário, data e operação), consultável por administradores
- Documentação completa da API com Swagger

## Como executar

### Opção 1: Docker Compose (recomendada)

Requisitos: Docker e Docker Compose.

```bash
git clone <url-do-repositorio>
cd loading-challenge
docker compose up --build
```

A primeira execução baixa as dependências e leva alguns minutos. Depois disso:

| Serviço | Endereço |
|---|---|
| Aplicação web | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

Para parar sem perder dados: `docker compose down`. Para apagar também o banco: `docker compose down -v`.

Para alterar senhas e portas, copie `.env.example` para `.env` e edite os valores.

### Opção 2: execução local sem Docker

Requisitos: JDK 21, Maven 3.9+, Node 20+ e PostgreSQL 14+.

1. Crie o banco:

   ```sql
   CREATE DATABASE loading_requests;
   ```

2. Suba a API (usa `postgres`/`postgres` em `localhost:5432` por padrão; ajuste com `DATABASE_URL`, `DATABASE_USER` e `DATABASE_PASSWORD`):

   ```bash
   cd backend
   mvn spring-boot:run
   ```

   As tabelas são criadas automaticamente pelo Flyway.

3. Suba o frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   Acesse http://localhost:5173. O Vite encaminha `/api` para `http://localhost:8080`.

### Acesso inicial

Na primeira execução é criado um administrador:

- E-mail: `admin@loading.dev`
- Senha: `Admin@12345`

Qualquer pessoa pode criar uma conta comum (perfil `USER`) pela tela de cadastro. Troque as credenciais do administrador em produção (veja [Configuração e produção](#configuração-e-produção)).

## Como testar cada funcionalidade

Os passos usam a interface web. Todos os endpoints também podem ser exercitados pelo Swagger UI (botão **Authorize** com o token do login).

**Cadastro e validação (RF01, RF06)**

1. Entre e abra **Nova solicitação**.
2. Envie o formulário vazio: cada campo obrigatório mostra uma mensagem.
3. Preencha tudo e salve: a solicitação nasce com o status **Aberta**.

**Consulta (RF02)**

1. Abra **Solicitações** para ver a lista.
2. Clique em um item para ver os detalhes, o andamento e o histórico.

**Filtros, busca e ordenação (RF03)**

1. Na lista, escolha um status e/ou uma prioridade.
2. Digite parte de um título, descrição ou categoria no campo de busca.
3. Troque a opção em **Ordenar por**.
4. Os filtros ficam na URL, então a página pode ser recarregada ou compartilhada.

**Fluxo de status (RF04, RF06)**

1. No detalhe de uma solicitação **Aberta**, o único botão disponível é **Iniciar atendimento**.
2. Depois de iniciar, o botão passa a ser **Concluir solicitação**.
3. Uma solicitação concluída não aceita mais mudanças.
4. A regra também vale na API. No Swagger, tente `PATCH /api/requests/{id}/status` com `{"status": "COMPLETED"}` em uma solicitação aberta: a resposta é `422` com uma mensagem explicando a etapa permitida.

**Histórico de status**

No detalhe, a seção **Histórico de status** lista cada mudança com autor e data.

**Edição e exclusão**

1. No detalhe, use **Editar** para alterar título, descrição, categoria ou prioridade. O status não muda por aqui.
2. Use **Excluir** e confirme a janela de confirmação.

**Persistência (RF05)**

1. Crie algumas solicitações.
2. Execute `docker compose restart` (ou pare e suba a aplicação novamente).
3. Entre de novo: os dados continuam lá.

**Autenticação e perfis**

1. Crie duas contas comuns em janelas diferentes: cada uma vê apenas as próprias solicitações, e acessar a de outra pessoa pela API retorna `403`.
2. Entre como administrador: ele vê todas as solicitações e o item **Auditoria** no menu.
3. Um usuário comum que acessa `/audit` é redirecionado, e a API responde `403`.

**Dashboard**

A página inicial mostra total, abertas, em andamento e concluídas. Os cartões levam à lista já filtrada. Usuários comuns veem os próprios números; administradores, os globais.

**Auditoria**

Como administrador, abra **Auditoria** para ver criações, edições, exclusões, mudanças de status, cadastros e logins, com usuário e data.

## Testes automatizados

```bash
cd backend
mvn test
```

- **Unitários** (sem Spring): regras de domínio (`ServiceRequest`, `RequestStatus`) e casos de uso com portas simuladas (criação, mudança de status, busca, cadastro de usuário).
- **Integração** (`ServiceRequestApiIntegrationTest`): sobe a aplicação com MockMvc e H2 em modo PostgreSQL, rodando as migrações reais. Cobre cadastro e login, validação, fluxo de status e transições inválidas, filtros, busca, ordenação, paginação, edição, exclusão, isolamento entre usuários, perfil de administrador, dashboard e auditoria.

## Arquitetura

O backend segue a **Arquitetura Hexagonal (Ports and Adapters)**. A regra de dependência aponta sempre para dentro: infraestrutura depende de aplicação, que depende de domínio.

```
backend/src/main/java/br/ufc/loading/requests/
├── domain/                  regras de negócio puras (sem Spring nem JPA)
│   ├── model/               ServiceRequest, User, StatusHistoryEntry, AuditEntry
│   ├── enums/               RequestStatus (máquina de transições), Priority, Role...
│   └── exception/           exceções de domínio
├── application/             casos de uso
│   ├── port/in/             portas de entrada (um caso de uso por interface)
│   ├── port/out/            portas de saída (repositórios, hash de senha, token, auditoria)
│   ├── usecase/             implementações dos casos de uso
│   ├── policy/              política de acesso (dono ou administrador)
│   ├── command/ e model/    comandos, consultas e resultados
└── infrastructure/          detalhes técnicos
    ├── adapter/in/web/      controllers REST, DTOs, mapeadores e tratamento global de erros
    ├── adapter/out/persistence/   entidades JPA, repositórios Spring Data e adaptadores
    ├── adapter/out/security/      JWT, BCrypt e filtro de autenticação
    └── config/              Spring Security, OpenAPI, injeção dos casos de uso, usuário admin inicial
```

Pontos principais:

- **A regra de status vive no domínio.** `RequestStatus.canTransitionTo` e `ServiceRequest.moveTo` decidem se uma transição é válida. Controllers e repositórios não contêm regra de negócio.
- **Validação em duas camadas.** Bean Validation nos DTOs devolve erros por campo. O domínio valida de novo, então a regra vale mesmo sem passar pelo controller.
- **Casos de uso sem dependência de framework**, exceto a anotação `@Transactional`. Eles são ligados por `UseCaseConfig`, e os testes unitários os executam com portas simuladas.
- **Erros padronizados.** `GlobalExceptionHandler` devolve sempre o mesmo formato (`ApiError`), com `fieldErrors` quando há erros de validação:

  | Situação | Status |
  |---|---|
  | Dados inválidos | 400 |
  | Sem autenticação ou credenciais inválidas | 401 |
  | Sem permissão | 403 |
  | Recurso inexistente | 404 |
  | E-mail já cadastrado | 409 |
  | Transição de status inválida | 422 |

**Frontend**: organizado por responsabilidade (`api`, `components`, `contexts`, `hooks`, `pages`, `routes`, `types`, `utils`). O cliente Axios injeta o token e trata sessão expirada em um único ponto. Cada tela tem estados de carregamento, erro (com nova tentativa), vazio e confirmação por notificação.

## Modelo de dados

| Tabela | Campos principais |
|---|---|
| `users` | id (UUID), name, email (único), password_hash (BCrypt), role (`ADMIN`/`USER`), created_at |
| `service_requests` | id, title, description, category, priority, status, owner_id (FK), owner_name, created_at, updated_at |
| `status_history` | id, request_id (FK, remoção em cascata), from_status, to_status, changed_by, changed_by_name, changed_at |
| `audit_log` | id, entity_type, entity_id, operation, user_id, user_name, occurred_at, details |

Índices em `status`, `priority`, `owner_id` e `created_at` para sustentar filtros e ordenação. O esquema é versionado em `backend/src/main/resources/db/migration`.

## API

Documentação interativa em `/swagger-ui.html` e especificação em `/v3/api-docs`.

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cadastra usuário (perfil `USER`) |
| POST | `/api/auth/login` | Autentica e retorna o token JWT |
| GET | `/api/requests` | Lista com `status`, `priority`, `search`, `sortBy`, `direction`, `page`, `size` |
| POST | `/api/requests` | Cria solicitação (status inicial `OPEN`) |
| GET | `/api/requests/{id}` | Consulta uma solicitação |
| PUT | `/api/requests/{id}` | Edita título, descrição, categoria e prioridade |
| DELETE | `/api/requests/{id}` | Exclui |
| PATCH | `/api/requests/{id}/status` | Avança o status (`OPEN` → `IN_PROGRESS` → `COMPLETED`) |
| GET | `/api/requests/{id}/history` | Histórico de status |
| GET | `/api/dashboard/summary` | Totais por status |
| GET | `/api/audit` | Auditoria (somente `ADMIN`) |

Valores aceitos: prioridade `LOW`, `MEDIUM`, `HIGH`; ordenação por `createdAt`, `updatedAt`, `title`, `priority` ou `status`; `size` de 1 a 100.

## Configuração e produção

Toda a configuração vem de variáveis de ambiente (veja `.env.example`):

| Variável | Finalidade |
|---|---|
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | Banco de dados |
| `JWT_SECRET` | Chave de assinatura dos tokens (mínimo de 32 caracteres) |
| `JWT_EXPIRATION_MINUTES` | Validade do token |
| `ADMIN_NAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Administrador criado na primeira execução |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para a API |
| `FRONTEND_PORT`, `BACKEND_PORT` | Portas expostas |

Para um ambiente real:

1. Defina valores próprios para `POSTGRES_PASSWORD`, `JWT_SECRET` e `ADMIN_PASSWORD`. Os padrões do Compose servem apenas para desenvolvimento.
2. Coloque um proxy com HTTPS na frente do Nginx do frontend.
3. Mantenha o volume `postgres_data` em armazenamento persistente e faça backup dele.
4. Os contêineres têm healthchecks e política `restart: unless-stopped`; o backend roda com usuário sem privilégios.

## Decisões de projeto

- **Categoria como texto livre.** O enunciado não define uma lista; o campo é obrigatório e limitado a 60 caracteres.
- **Transição estrita.** Além de impedir saltos, a API também rejeita retrocessos e mudanças após a conclusão.
- **Perfis simples.** `USER` gerencia só as próprias solicitações; `ADMIN` enxerga e altera todas e acessa a auditoria. Contas novas são sempre `USER`.
- **JWT stateless.** Evita estado de sessão no servidor e simplifica a execução em contêineres.
- **Nomes denormalizados.** Histórico e auditoria guardam o nome de quem agiu no momento da ação, para não depender de cadastros futuros.
- **Prioridade e status como inteiros no banco**, na ordem do enum, para que a ordenação por prioridade seja a esperada (baixa, média, alta).

## Uso de inteligência artificial

> Ajuste esta seção para refletir exatamente como você usou as ferramentas.

Este projeto foi desenvolvido com apoio do Claude (Anthropic), usado para propor a arquitetura, gerar a primeira versão do código e dos testes e redigir esta documentação. O código foi revisado, executado e validado pelo autor, que consegue explicar as decisões de projeto, a regra de transição de status, o fluxo de autenticação e a organização em camadas descritos acima.
