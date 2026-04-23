# CLAUDE.md

> Responda em **pt-BR**. Curto e direto.

## Repositório

Serviço **CRM** (clientes, conversas, registros) de plataforma WhatsApp bot. Inbound Webhook e Bot Engine ficam em outros repos.

Stack: Kotlin 2.2.21 · Spring Boot 4.0.2 · PostgreSQL · JWT · JDK 21 (ignore README que cita Java 25).

## Specs obrigatórias

Antes de implementar mudanças, considerar e seguir as specs em `docs/specs/README.md` e documentos referenciados.

- Commits devem seguir `docs/specs/semantic-commits.md`
- Convenções REST devem seguir `docs/specs/rest-standardization.md`
- Fluxos de imagem/upload devem seguir `docs/specs/uploads.md`
- Ao mencionar telas, consultar `../crm-front/docs/specs/frontend/README.md`

## Comandos

```bash
cd infra-crm && docker compose up -d        # infra (obrigatória p/ bootRun e testes de integração)
./gradlew bootRun                           # app (.\gradlew.bat no Windows)
./gradlew detektFast test                   # fluxo local rápido (sem clean)
./gradlew test --tests "FQCN"               # teste específico
./gradlew test --tests "com.example.crm.application.usecase.*"  # pacote específico
./gradlew detekt                            # lint completo
./gradlew integrationTest                   # integração
./gradlew build                             # completo (CI/PR-like)
docker compose down -v && docker compose up -d  # reset DB (dentro de infra-crm)
```

Evite por padrão: `./gradlew clean build`.

## Estrutura do projeto

- `domain/model` → data classes puras, sem infra
- `domain/repository` → contratos de repositório
- `application/port/input` → interfaces de use case
- `application/usecase` → implementações `@Service`
- `infrastructure/web/controller` → endpoints REST
- `infrastructure/web/dto` → request/response
- `infrastructure/web/mapper` → DTO ↔ domínio (`WebMappers.kt`)
- `infrastructure/persistence/entity` → `@Entity` JPA
- `infrastructure/persistence/repository` → Spring Data
- `infrastructure/persistence/adapter` → implementações dos repositórios de domínio
- `infrastructure/persistence/mapper` → entidade ↔ domínio
- `infrastructure/security` → JWT, filtros, seeders
- `architecture/` → testes ArchUnit

**Novo recurso `Foo`:** criar nessa ordem — model → repo (domain) → useCase (port + impl) → entity → jpaRepo → persistenceMapper → adapter → request/response DTOs → estender `WebMappers` → controller.

## Regras ArchUnit (quebram build)

- `domain` não depende de `infrastructure`
- `application.usecase` não depende de `infrastructure`
- Controllers só dependem de `application`, `domain`, `infrastructure.web.*`
- `*UseCaseImpl` deve ter `@Service`
- `*Controller` deve estar em `..infrastructure.web.controller..`

## Banco

- `ddl-auto: none` — Hibernate não altera schema
- DDL/seed: `infra-crm/postgres/init/` (`a*.sql` schema · `b*.sql` dados), ordem alfabética
- Migrações pós-criação: Flyway em `src/main/resources/db/migration/V*__*.sql`
- Tabela `"user"` exige aspas (palavra reservada)

## Testes

- Mock: **MockK** (`io.mockk`). Nunca Mockito.
- `test` → unitários + arquitetura (rápido)
- `integrationTest` → somente `@Tag("integration")` (mais lento, requer docker up)
- `UseCasesTest` → unitários sem Spring, todos use cases num arquivo
- `CrmApplicationTests` → `@SpringBootTest` marcado como integração
- ArchUnit em `architecture/` roda junto com `./gradlew test`

## Segurança

- JWT stateless. Auth obrigatório exceto `/api/v1/auth/**`, Swagger, actuator
- Token: `POST /api/v1/auth/token` `{"email":"admin@saas.com","password":"123456"}`
- `AdminSeeder` cria admin no startup (tenant_id=1)
- BCrypt: `$2b$` é normalizado para `$2a$`

## URLs (ambiente de testes)

| URL                                                 | Uso          |
| --------------------------------------------------- | ------------ |
| `https://api-crm.luizlab.com/swagger-ui/index.html` | Swagger      |
| `https://api-crm.luizlab.com/v3/api-docs`           | OpenAPI JSON |
| `https://api-crm.luizlab.com/health/live`           | Health       |

**Local (desenvolvimento):**
| URL | Uso |
|---|---|
| `http://localhost:8080/swagger-ui/index.html` | Swagger local |
| `http://localhost:5050` | pgAdmin (`admin@crm.com`/`admin`) |
| `localhost:5432` | Postgres (`crm`/`crm`/`crm`) |

## Fluxo de trabalho

- **API/contrato:** consultar `/v3/api-docs` antes de inferir rota ou payload
- **Bug:** ir direto ao controller → use case → adapter; não varrer o repo
- **Nova feature:** seguir ordem da seção Estrutura; reutilizar padrões existentes
- **Mudança mínima:** alterar só o necessário; sem refactor fora de escopo
- **Validação automática:** hook em `.claude/settings.json` roda `detektFast` + `test` (apenas arquivos `.kt` alterados) após cada Edit/Write, em background
- **Validação local:** `detektFast` + `test` sem `clean` (ou aguarde feedback automático do hook)
- **Validação CI/PR:** `detekt` + `build` (inclui integração)
- **Gate final obrigatório:** `./gradlew detekt` + `./gradlew test` verdes antes de concluir
- **Commit/Push/PR:** Só fazer se **último prompt pediu explicitamente**. Caso contrário, **NUNCA** fazer sem confirmação — aguarde comando do usuário
  - Se pedido explícito: executar sem pedir permissão
  - Se não pedido: sempre pedir confirmação antes

## Build otimizado (arquivos alterados apenas)

```bash
# Compilação rápida de arquivos alterados (sem clean)
./gradlew detektFast test --no-build-cache -x detektMain

# Lint + testes apenas de arquivos modificados (mais rápido)
./gradlew detektFast test

# Para build completo (CI/PR, inclui integração)
./gradlew build
```

**Dicas:**

- Evitar `./gradlew clean build` (recompila tudo)
- `--no-build-cache` força recompilação do Kotlin (útil se cache ficou inconsistente)
- `-x detektMain` pula lint do main (usa `detektFast` em background)
- Hook automático já roda `detektFast` + `test` em background após edições

## Regras de economia de tokens

- Ler apenas arquivos estritamente necessários
- Sempre usar Grep/Glob antes de Read
- Nunca ler diretórios inteiros sem necessidade
- Não resumir arquivos sem pedido explícito
- Não repetir o pedido do usuário
- Não gerar planos longos antes de agir
- Não propor alternativas múltiplas sem pedido
- Não explorar amplamente sem direção clara
- Não refatorar fora do escopo
- Não criar abstrações desnecessárias
