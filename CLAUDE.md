# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> Responda sempre em **pt-BR**, respostas curtas e diretas.

---

## O que é este repositório

Serviço **CRM** — fonte da verdade de clientes, conversas e registros de negócio — dentro de uma plataforma maior de WhatsApp bot. Os outros serviços (Inbound Webhook, Bot Engine) **não estão neste repo**.

Stack: Kotlin 2.2.21 · Spring Boot 4.0.2 · PostgreSQL · JWT · JVM 21.

> README diz Java 25 — ignore. O build usa JDK 21 (`build.gradle.kts`). Confie no build.

---

## Comandos principais

```bash
# 1. Subir infraestrutura (obrigatório antes de bootRun ou testes de integração)
cd infra-crm && docker compose up -d

# 2. Rodar a aplicação
./gradlew bootRun          # Linux/Mac
.\gradlew.bat bootRun      # Windows

# 3. Todos os testes (JaCoCo gerado automaticamente ao final)
./gradlew test

# 4. Classe de teste específica
./gradlew test --tests "com.example.crm.application.usecase.UseCasesTest"

# 5. Lint (Detekt)
./gradlew detekt
```

**Gate obrigatório antes de concluir qualquer tarefa com alteração de código:** executar `detekt` e `test`. Se houver falha, corrigir e rodar novamente até passar.

**Ordem recomendada:** implementar → lint → testes do escopo → suite completa.

---

## Arquitetura

Arquitetura **Hexagonal + DDD** em três camadas principais:

```
domain/           → modelos puros (data classes sem anotações de infra)
application/      → use cases (portas + implementações)
infrastructure/   → web (controllers/DTOs), persistence (JPA/adapters), security
```

**Fluxo para cada recurso:**

1. `domain/model/Foo.kt` — data class pura
2. `domain/repository/FooRepository.kt` — interface
3. `application/port/input/FooUseCase.kt` — interface
4. `application/usecase/FooUseCaseImpl.kt` — `@Service`
5. `infrastructure/persistence/entity/FooJpaEntity.kt` — `@Entity`
6. `infrastructure/persistence/repository/FooJpaRepository.kt` — Spring Data
7. `infrastructure/persistence/mapper/FooPersistenceMapper.kt`
8. `infrastructure/persistence/adapter/FooRepositoryAdapter.kt` — `@Component`
9. `infrastructure/web/dto/request/FooRequest.kt`
10. `infrastructure/web/dto/response/FooResponse.kt`
11. `infrastructure/web/mapper/WebMappers.kt` — estender com mapeamentos do Foo
12. `infrastructure/web/controller/FooController.kt`

**Regras ArchUnit (violações quebram o build):**

| Regra |
|---|
| `domain` não depende de `infrastructure` (nem indiretamente) |
| `application.usecase` não depende de `infrastructure` |
| Controllers só dependem de `application`, `domain`, `infrastructure.web.*` |
| Classes `*UseCaseImpl` devem ter `@Service` |
| Classes `*Controller` devem estar em `..infrastructure.web.controller..` |

---

## Schema do banco

- `ddl-auto: none` — Hibernate **não** cria nem altera tabelas.
- Schema gerenciado pelos scripts em `infra-crm/postgres/init/` (ordem alfabética).
  - `a*.sql` = DDL de schema · `b*.sql` = seed/dados iniciais
- Migrações pós-criação: `src/main/resources/db/migration/V*__*.sql` (Flyway).
- Tabela `user` usa aspas (`"user"`) por ser palavra reservada no PostgreSQL.
- Resetar banco: `docker compose down -v && docker compose up -d` (dentro de `infra-crm`).

---

## Testes

- **Mocking:** MockK (`io.mockk`). Não usar Mockito.
- `UseCasesTest` — unitários sem Spring, todos os use cases em um arquivo.
- `CrmApplicationTests` — `@SpringBootTest`, requer `docker compose up -d`.
- Testes de arquitetura em `architecture/` rodam junto com `./gradlew test`.
- JaCoCo é gerado automaticamente; não é necessário chamar tarefa separada.

---

## Segurança

- JWT stateless. Todos os endpoints requerem `Authorization: Bearer <token>` exceto `/api/v1/auth/**`, Swagger e actuator.
- Obter token: `POST /api/v1/auth/token` com `{"email": "admin@saas.com", "password": "string"}`.
- `AdminSeeder` garante o usuário admin no startup (tenant_id=1).
- BCrypt: prefixo `$2b$` é normalizado para `$2a$` automaticamente.

---

## URLs locais

| URL | Descrição |
|---|---|
| `http://localhost:8080/swagger-ui/index.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |
| `http://localhost:8080/health/live` | Health check |
| `http://localhost:5050` | pgAdmin (`admin@crm.com` / `admin`) |
| `localhost:5432` | PostgreSQL (`crm`/`crm`/`crm`) |
