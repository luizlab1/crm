# Engineering Specs

This directory defines engineering conventions for this repository.

## Product context

- This API is for a CRM focused on marketing and automated sales.
- Current scope is a lean MVP for beauty salons (MEI and ME in Brazil).
- Next versions will expand to other niches (for example: restaurants, delivery, and sales force operations) using the same core structure.
- Therefore, implementations should stay lean for the current MVP while preserving scalability for future vertical expansion.

## Frontend screens reference

When mentioning screens (UI pages/flows), always consult:
`../crm-front/specs/frontend/README.md`

## Test environment

- The test environment API docs URL is: `https://api-crm.luizlab.com/v3/api-docs`
- You can check available environment endpoints at: `https://api-crm.luizlab.com/v3/api-docs`

## Token and test data policy

When creating/updating test data and authentication is needed, use:

```json
{
  "email": "admin@saas.com",
  "password": "123456"
}
```

- Always ask for permission before generating a token.
- Always ask for permission before creating or changing test data.
- Exception: do not ask when the user explicitly requests token generation and/or test data changes.

## Specs index

- [Semantic Commits](./semantic-commits.md)
- [REST API Standardization](./rest-standardization.md)
- [Upload Support](./uploads.md)
