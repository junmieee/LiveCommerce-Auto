# Documentation Preference: API Specs

Owner’s preference: When asked for API documentation/specs, respond using this Notion‑friendly Markdown format. Keep it concise, consistent, and copy‑paste ready.

## Format Rules

- Title: `# <Domain> API`
- Short purpose bullets (what the API covers)
- Security section: auth header, role/membership rules, common error mapping
- Common query params table (if applies): `page, limit, search, sort`
- Response models summary (key DTOs)
- API summary table with columns: `API 번호, 기능명, Method, URL, 인증, 설명`
- Per‑endpoint sections (ordered):
  - `## <API 번호>.<기능명>`
  - Method/URL/Auth/Success/Failure bullets
  - Request Body or Query Params as a table
  - Response example (JSON fenced block)
  - Optional curl example if helpful

## Tables

Use GitHub/Notion Markdown tables:

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | string | ✅ | 설명 |

## Error Mapping (default)

- 400 Bad Request: validation/parameter errors
- 401 Unauthorized: missing/expired access token
- 403 Forbidden: membership/role mismatches
- 404 Not Found: resource not found or hidden

## Example Header Block

```
# Product API

- 목적: 공개 상품 조회 + 판매자 전용 관리
- 공개/인증: 공개 GET /api/products/**, 인증 /api/seller/**
```

## Example API Summary Table

| API 번호 | 기능명 | Method | URL | 인증 | 설명 |
| --- | --- | --- | --- | --- | --- |
| PRODUCT001 | 공개 상품 목록 | GET | /api/products | ✗ | isActive=true 목록 |
| PRODUCT002 | 공개 상품 상세 | GET | /api/products/{id} | ✗ | isActive=true 상세 |
| PRODUCT003 | 상품 등록 | POST | /api/seller/products | ✓ | 새 상품 등록 |

## Notes

- Keep examples realistic but short.
- Prefer camelCase in request/response unless legacy requires snake_case.
- If security/ownership rules apply, call them out early in the doc.

