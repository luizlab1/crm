# Semantic Commits Spec

All commit messages must use semantic prefixes and be written in English.

## Format

Use:

`<type>(optional-scope): <short imperative summary>`

Examples:

- `feat(auth): add token refresh endpoint`
- `fix(customers): prevent null pointer on list response`
- `refactor(leads): simplify status mapping`
- `docs(specs): document REST pagination rules`

## Allowed types

- `feat`: new functionality
- `fix`: bug fix
- `refactor`: code change without behavior change
- `docs`: documentation only
- `test`: tests only
- `chore`: tooling/build/maintenance
- `perf`: performance improvement

## Rules

- Write in English.
- Keep the subject concise and specific.
- Prefer imperative verbs (`add`, `fix`, `remove`, `update`).
- Do not use generic subjects like `changes` or `updates`.
