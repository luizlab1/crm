# Infraestrutura do Projeto (infra)

Este diretório contém os artefatos de **infraestrutura local** usados
para desenvolvimento e testes, desacoplados do código da aplicação.

Atualmente inclui: - PostgreSQL - pgAdmin

## Pré-requisitos

-   Docker
-   Docker Compose (v2+)

Verificação rápida: docker --version docker compose version

## Subir a infraestrutura

A partir da raiz do projeto:

docker compose -f infra/docker/docker-compose.yml up -d

Serviços iniciados: - PostgreSQL: localhost:5432 - pgAdmin:
http://localhost:5050

## Credenciais de Sandbox

### PostgreSQL

-   Host: localhost
-   Porta: 5432
-   Database: crm
-   Usuário: crm
-   Senha: crm

### pgAdmin

-   URL: http://localhost:5050
-   Email: admin@crm.local
-   Senha: admin

Para conectar no pgAdmin: - Host: postgres - Porta: 5432 -
Database/User/Password: conforme acima

## Parar a infraestrutura

docker compose -f infra/docker/docker-compose.yml down

## Remover dados (reset completo)

Remove volumes e dados do banco.

docker compose -f infra/docker/docker-compose.yml down -v

## Observações

-   Este diretório não faz parte do build da aplicação.
-   Ideal para evoluir futuramente com Kafka, Redis, Keycloak,
    Kubernetes e Terraform.

Regra de ouro: infra/ sobe dependências externas src/ contém apenas
código da aplicação
