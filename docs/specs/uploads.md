# Upload Support Spec

This spec is based on the test environment OpenAPI docs:
`https://api-crm.luizlab.com/v3/api-docs`

## When to use uploads

- If the request is to make an image dynamic, image-driven, or API-managed, use the Upload API support.
- Do not introduce alternative image persistence flows when Upload API already covers the use case.

## Supported endpoints

- `GET /api/v1/uploads`
  - List uploads by filters (`fileType`, `entityId`) with pagination (`page`, `size`).
- `POST /api/v1/uploads`
  - Upload file using `multipart/form-data`.
  - Required query params: `fileType`, `entityId`.
  - Optional query params: `sortOrder`, `title`, `subtitle`, `width`, `height`, `quality`.
  - Request body: form field `file` (binary).
- `GET /api/v1/uploads/{id}`
  - Retrieve upload metadata.
- `PATCH /api/v1/uploads/{id}`
  - Update metadata (`fileType`, `entityId`, `sortOrder`, `title`, `subtitle`).
- `DELETE /api/v1/uploads/{id}`
  - Remove upload.
- `GET /api/v1/uploads/{id}/view`
  - Stream/display file content.
- `GET /api/v1/uploads/{id}/download`
  - Download file content.
- `GET /api/v1/uploads/rules`
  - Retrieve upload rules (including quality limits).

## Implementation guidance

- Persist and use upload metadata from `UploadResponse` (for example: `id`, `fileType`, `entityId`, `title`, `subtitle`, `sortOrder`, `viewUrl`, `downloadUrl`).
- Prefer serving image display via upload URLs (`viewUrl`/`downloadUrl`) returned by the API.
- Keep list endpoints paginated when exposing uploads in resource listings.

## Image dynamic behavior rule

- Whenever the user asks to "dynamize" images or "use API for images", the implementation must be based on the Upload API endpoints above.
