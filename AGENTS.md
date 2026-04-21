# AGENTS.md

Guia de build otimizado para agents (OpenCode, Anthropic SDK, etc).

**Ambiente de testes:** `https://api-crm.luizlab.com/` · Credenciais: `admin@saas.com` / `123456`

## Build rápido (arquivos alterados)

Ao fazer alterações de código, use **sempre** um desses comandos para validação rápida:

```bash
# ✅ RECOMENDADO: Lint + testes de arquivos alterados (mais rápido)
./gradlew detektFast test --no-build-cache -x detektMain

# ✅ ALTERNATIVA: Se cache está ok (mais rápido ainda)
./gradlew detektFast test
```

**Não use:**
- ❌ `./gradlew clean build` — recompila TUDO (muito lento)
- ❌ `./gradlew detekt test` — lint completo (lento)

## Flags importantes

| Flag | Efeito | Quando usar |
|------|--------|------------|
| `--no-build-cache` | Força recompilação Kotlin | Cache inconsistente ou mudanças não refletem |
| `-x detektMain` | Pula lint do main (usa detektFast) | Build local rápido (hook roda detektMain em background) |
| `--tests "FQCN"` | Roda teste específico | Debug de um teste apenas |

## Fluxo recomendado para agents

1. **Após editar arquivos .kt:**
   ```bash
   ./gradlew detektFast test --no-build-cache -x detektMain
   ```
   - Rápido (5-15 min para arquivos alterados)
   - Detecta issues locais
   - Pula checks desnecessários

2. **Antes de commit/PR (gate final):**
   ```bash
   ./gradlew detekt && ./gradlew test
   ```
   - Lint + testes completos
   - Garante CI/PR vai passar

3. **Build completo (apenas se necessário):**
   ```bash
   ./gradlew build
   ```
   - Inclui integrationTest
   - Requer Docker up (`cd infra-crm && docker compose up -d`)

## Troubleshooting

**Testes falham com "class not found":**
```bash
./gradlew clean compileKotlin test --no-build-cache
```

**Cache está estranho:**
```bash
rm -rf .gradle build && ./gradlew detektFast test --no-build-cache
```

**Precisa testar integração:**
```bash
cd infra-crm && docker compose up -d  # primeira vez
./gradlew build  # inclui integrationTest
```

## Commit e Push

**Regra importante:**
- ✅ **Se o último prompt pediu explicitamente:** fazer commit/push **sem pedir permissão**
- ❌ **Se não pediu:** **NUNCA** fazer sem confirmação — aguarde comando do usuário

Exemplos:
```bash
# ✅ Último prompt: "faça commit dessa feature"
git commit -m "feat: ..."  # fazer direto, sem pedir permissão

# ❌ Último prompt: não mencionou commit/push
# Resultado: propor commit, aguardar confirmação do usuário antes de fazer
```

**Outras regras mantidas:**
- Sempre usar `./gradlew detekt` + `./gradlew test` verdes antes de commit
- Criar NEW commits (não amend) se hook falhar
- Verificar git status, git diff antes de propor commit
- Nunca fazer `--force push` para main/master

## Hooks automáticos

O projeto tem hook em `.claude/settings.json` que roda `detektFast` + `test` **em background** após cada Edit/Write de `.kt`. Não precisa rodar manualmente se aguardar feedback automático do hook (mais lento, mas não bloqueia).

Para validação imediata: roda comando acima manualmente.
