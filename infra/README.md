# Infraestrutura do Projeto (`infra`)

Este diretório contém os artefatos de **infraestrutura local**
utilizados para desenvolvimento e testes, mantidos **desacoplados do
código da aplicação**.

Atualmente inclui: - PostgreSQL - pgAdmin

------------------------------------------------------------------------

## Pré-requisitos

-   Docker
-   Docker Compose (v2+)

Verificação rápida:

``` bash
docker --version
docker compose version
```

------------------------------------------------------------------------

## Subir a infraestrutura

A partir da **raiz do projeto**:

``` bash
docker compose -f crm-docker/docker-compose.yml up -d
```
Serviços iniciados: - **PostgreSQL**: `localhost:5432` - **pgAdmin**:
[http://localhost:5050](http://localhost:5050)
---

### Parar a infraestrutura

``` bash
docker compose -f crm-docker/docker-compose.yml down
```
---

### Resetar a infra (preserva volumes)

``` bash bash
docker compose -f crm-docker/docker-compose.yml down -v; docker compose -f crm-docker/docker-compose.yml up -d
```

---

### Deletar Infra (exclusão completa)

⚠️ **Remove volumes e dados persistidos do banco**.

``` bash
docker compose -f crm-docker/docker-compose.yml down -v
```




------------------------------------------------------------------------

## Credenciais de Sandbox

### PostgreSQL

-   Host: `localhost`
-   Porta: `5432`
-   Database: `crm`
-   Usuário: `crm`
-   Senha: `crm`

### pgAdmin

-   URL: [http://localhost:5050](http://localhost:5050)
-   Email: `admin@crm.local`
-   Senha: `admin`

Para registrar o servidor no pgAdmin: - Host: `postgres` (nome do
serviço no Docker) - Porta: `5432` - Database / User / Password:
conforme acima
---
Para gerar um **Diagrama de Entidade e Relacionamento (DER)**, siga os passos:
1. Acesse [http://localhost:5050](http://localhost:5050) e faça login com as credenciais acima.
2. No painel esquerdo, clique sobre o banco, depois clique em `ERD For Database`.


------------------------------------------------------------------------

## Observações

-   Este diretório **não participa do build** da aplicação.
-   Estruturado para evolução futura com:
    -   Kafka
    -   Redis
    -   Keycloak
    -   Kubernetes
    -   Terraform

### Regra de ouro

> `infra/` → sobe dependências externas\
> `src/` → contém apenas código da aplicação
