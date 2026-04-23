# REST API Standardization Spec

This document defines REST conventions for new and updated endpoints.

## Scope

- Existing routes must not be refactored unless explicitly requested.
- For new routes, always follow this spec.
- For changes in existing routes, ask first whether the route should also be refactored to match this spec.

## Collection endpoints (list routes)

- Every list route must support pagination.
- Do not return full entities in `findAll`/list responses.
- List responses must return only fields commonly needed for listing/display (summary projection).

Examples of typical list fields:

- `id`
- primary display name/title
- status
- key timestamps (`createdAt`, `updatedAt`) when relevant

Detailed/full object fields should be returned by detail endpoints (for example, `GET /resource/{id}`).

## Naming and behavior

- Keep resource-oriented paths and HTTP verb semantics.
- Ensure filtering/sorting conventions are consistent within each resource.
- Keep response shape stable and predictable.
