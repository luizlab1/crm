# AGENTS.md

> Responda sempre em **pt-BR**, respostas curtas e diretas.

---

## O que é este repositório

Serviço **CRM** — fonte da verdade de clientes, conversas e registros de negócio — dentro de uma plataforma maior de WhatsApp bot. Os outros serviços (Inbound Webhook, Bot Engine) **não estão neste repo**.

Stack: Kotlin 2.2.21 · Spring Boot 4.0.2 · PostgreSQL · JWT · JVM 21.

---

## Comandos principais

```powershell
# 1. Subir infraestrutura (obrigatório antes de bootRun ou integrationTest)
cd infra-crm
docker compose up -d

# 2. Rodar a aplicação
.\gradlew.bat bootRun

# 3. Fluxo local rápido (sem clean)
.\gradlew.bat detektFast test

# 4. Lint completo
.\gradlew.bat detekt

# 5. Testes de integração (mais lentos)
.\gradlew.bat integrationTest

# 6. Validação completa (CI/PR-like)
.\gradlew.bat build
```

**Rodar uma classe de teste específica:**
```powershell
.\gradlew.bat test --tests "com.example.crm.application.usecase.UseCasesTest"
```

**Rodar pacote específico:**
```powershell
.\gradlew.bat test --tests "com.example.crm.application.usecase.*"
```

**Evite por padrão:** `clean build` (quebra incremental/caches locais).

**Ordem recomendada:** implementar → `detektFast` + testes do escopo → integração quando necessário → `build` antes de PR.

**Gate obrigatório antes de concluir qualquer tarefa com alteração de código:**
- Se houve mudança em código-fonte, o agente deve executar `./gradlew.bat detekt` e `./gradlew.bat test` (ou `\.\gradlew.bat` no PowerShell).
- Se algum dos comandos ainda não foi executado na sessão após as mudanças, deve executar antes de marcar como pronto.
- Se houver falha, o agente deve corrigir o problema e rodar novamente até passar.
- **Validação automática:** hook em `.claude/settings.json` roda `detektFast` + `test` (apenas arquivos `.kt` alterados) após cada Edit/Write, em background — aguarde seu resultado antes de marcar como pronto.

**Sobre commit, push e pull requests:**
- **NUNCA** fazer commit, push ou criar PR sem solicitação explícita do usuário.
- Alterações devem ficar em staged/uncommitted até o usuário solicitar.
- Se o usuário pedir "pronto", "concluído" ou similar, apenas confirmar que o código está pronto — não faça commit automaticamente.

> README diz Java 25 — ignore. O build usa JDK 21 (`build.gradle.kts:26`). Confie no build.

---

## Estrutura do projeto

```
crm/
├── build.gradle.kts          # única fonte de verdade do toolchain
├── infra-crm/
│   ├── docker-compose.yml    # PostgreSQL 17 + pgAdmin
│   └── postgres/init/        # SQL de schema e seed (ordem alfabética)
│       ├── a*.sql            # DDL de schema
│       └── b*.sql            # seed / dados de teste
└── src/
    ├── main/kotlin/com/example/crm/
    │   ├── CrmApplication.kt
    │   ├── domain/
    │   │   ├── model/        # entidades de domínio (data classes puras)
    │   │   ├── repository/   # interfaces de repositório
    │   │   └── exception/    # EntityNotFoundException
    │   ├── application/
    │   │   ├── port/input/   # interfaces de use case (*UseCase)
    │   │   └── usecase/      # implementações (*UseCaseImpl) — @Service obrigatório
    │   └── infrastructure/
    │       ├── web/
    │       │   ├── controller/   # *Controller
    │       │   ├── dto/          # request/ e response/
    │       │   └── mapper/       # WebMappers.kt
    │       ├── persistence/
    │       │   ├── adapter/      # *RepositoryAdapter implements domínio
    │       │   ├── entity/       # entidades JPA
    │       │   ├── mapper/       # *PersistenceMapper
    │       │   └── repository/   # *JpaRepository (Spring Data)
    │       ├── security/         # JWT (JwtService, SecurityConfig)
    │       ├── startup/          # AdminSeeder
    │       └── config/
    └── test/kotlin/com/example/crm/
        ├── architecture/         # ArchUnit — violações quebram o build
        ├── application/usecase/  # UseCasesTest (MockK, sem Spring)
        └── CrmApplicationTests   # @SpringBootTest — requer Docker rodando
```

---

## Domínio — modelos principais

| Modelo | Descrição |
|---|---|
| `Tenant` | empresa/cliente da plataforma (multi-tenant) |
| `User` | usuário autenticado, vinculado a tenant |
| `Person` | pessoa física referenciada por Worker/User |
| `Worker` | funcionário de um tenant |
| `Customer` | cliente atendido pelo CRM |
| `Lead` | oportunidade de negócio, vinculada a `PipelineFlow` |
| `Order` | pedido de um customer |
| `Appointment` | agendamento |
| `Schedule` | vínculo tenant/customer/appointment |
| `Item` / `ItemCategory` | catálogo de produtos/serviços |
| `PipelineFlow` | etapas do funil de vendas |
| `Address` / `City` / `State` / `Country` | endereçamento — Country/State/City são read-only |

Todos os modelos usam `OffsetDateTime` para timestamps e `bigint GENERATED ALWAYS AS IDENTITY` como PK no banco.

---

## Schema — regras do banco

- `ddl-auto: none` — o Hibernate **não** cria nem altera tabelas.
- Schema gerenciado exclusivamente pelos scripts em `infra-crm/postgres/init/`.
- Scripts rodam em **ordem alfabética** na criação do container.
  - `a*` = DDL de schema · `b*` = seed/dados iniciais
- Para adicionar migração: siga o prefixo (`a010-...`, `b03-...`).
- Resetar banco: `docker compose down -v && docker compose up -d` (dentro de `infra-crm`).
- Tabela `user` é quoted (`"user"`) por ser palavra reservada no PostgreSQL.

---

## Arquitetura hexagonal — regras ArchUnit (quebram o build)

| Regra | Detalhe |
|---|---|
| `domain` não depende de `infrastructure` | nem indiretamente |
| `application.usecase` não depende de `infrastructure` | isolamento total |
| Controllers só dependem de `application`, `domain`, `infrastructure.web.*` | |
| Classes `*UseCaseImpl` devem ter `@Service` | |
| Classes `*Controller` devem estar em `..infrastructure.web.controller..` | |
| Classes `*Repository` devem estar em `domain.repository` ou `infrastructure.persistence.repository` | |

**Padrão de implementação (para cada novo recurso):**
1. `domain/model/Foo.kt` — data class pura
2. `domain/repository/FooRepository.kt` — interface
3. `application/port/input/FooUseCase.kt` — interface
4. `application/usecase/FooUseCaseImpl.kt` — `@Service`
5. `infrastructure/persistence/entity/FooEntity.kt` — `@Entity`
6. `infrastructure/persistence/repository/FooJpaRepository.kt` — Spring Data
7. `infrastructure/persistence/mapper/FooPersistenceMapper.kt`
8. `infrastructure/persistence/adapter/FooRepositoryAdapter.kt` — `@Component`
9. `infrastructure/web/dto/request/FooRequest.kt`
10. `infrastructure/web/dto/response/FooResponse.kt`
11. `infrastructure/web/mapper/` — estender `WebMappers.kt`
12. `infrastructure/web/controller/FooController.kt`

---

## Testes

- **Mocking:** MockK (`io.mockk`). Não usar Mockito.
- `test` — unitários + arquitetura (rápido, sem integração).
- `integrationTest` — apenas testes com `@Tag("integration")` (mais lento, requer Docker up).
- `UseCasesTest` — unitários sem Spring, todos os use cases em um arquivo.
- `CrmApplicationTests` — `@SpringBootTest` e marcado como integração.
- Testes de arquitetura (`architecture/`) rodam no `test` e falham se regras forem violadas.

---

## Segurança

- Autenticação JWT stateless. Todos os endpoints requerem `Authorization: Bearer <token>` exceto `/api/v1/auth/**`, Swagger e actuator.
- Obter token: `POST /api/v1/auth/token` com `{"email": "admin@saas.com", "password": "string"}`.
- `AdminSeeder` garante o usuário admin no startup (tenant_id=1, email `admin@saas.com`).
- Compatibilidade BCrypt: prefixo `$2b$` é normalizado para `$2a$` automaticamente.

---

## URLs locais

| URL | Descrição |
|---|---|
| `http://localhost:8080/swagger-ui/index.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |
| `http://localhost:8080/health/live` | Health check → `{"status":"UP"}` |
| `http://localhost:5050` | pgAdmin (email: `admin@crm.com`, senha: `admin`) |
| `localhost:5432` | PostgreSQL (db/user/senha: `crm`) |

---

## Worktree workflow

O agente `.opencode/agent/parallel-coordinator.md` gerencia git worktrees. Novas worktrees são **sempre** criadas a partir de `master`. Ao iniciar uma sessão, verificar worktrees existentes antes de criar uma nova.
