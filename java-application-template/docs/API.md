# API Reference

The full interactive spec is generated from the code and always current — use it alongside this doc, not instead of it:
- Swagger UI: `/swagger-ui.html`
- Raw OpenAPI JSON: `/v3/api-docs`

(Both can be turned off per environment — see `springdoc.*` in [CONFIGURATION.md](CONFIGURATION.md).)

## Base URL
Single environment today — there's no separate staging/prod host in this template.

| Environment | URL |
|---|---|
| Local / any deployment | `http://<host>:<port>/api/v1` (default `http://localhost:8080/api/v1`) |

## Auth
None. There is no authentication or authorization on the task endpoints in this template — anyone who can reach the port can read and write all tasks. If you deploy this beyond local/internal use, add auth before exposing it (e.g. Spring Security + a token scheme) and update this section.

## Conventions

**Pagination**: not implemented — `GET /api/v1/tasks` returns the full list as a plain JSON array. Fine for a small task list; add pagination before this grows large.

**Error shape**: Spring Boot's default error body (no custom `@RestControllerAdvice` in this template yet):
```json
{
  "timestamp": "2026-09-08T10:15:30.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/v1/tasks/999"
}
```

**Validation errors** (400) use Spring's default `MethodArgumentNotValidException` shape — confirm exact field names against the `Task` request DTO, but the structure looks like:
```json
{
  "timestamp": "2026-09-08T10:15:30.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "errors": [
    { "field": "title", "defaultMessage": "must not be blank" }
  ]
}
```
If you add a `@RestControllerAdvice` to produce a cleaner error body, update both examples above.

**Versioning**: path-based, `/api/v1/...`. No deprecation policy defined yet since there's only one version.

## Endpoints

### Tasks

#### `GET /api/v1/tasks`
List all tasks.

- Query params: none currently.
- Response: `200 OK`
```json
[
  { "id": 1, "title": "Write release notes", "description": "For this release" }
]
```

```bash
curl http://localhost:8080/api/v1/tasks
```

#### `GET /api/v1/tasks/{id}`
Get one task.

- Path params: `id` (integer, required).
- Response: `200 OK`
```json
{ "id": 1, "title": "Write release notes", "description": "For this release" }
```
- Errors: `404 Not Found` if `id` doesn't exist.

```bash
curl http://localhost:8080/api/v1/tasks/1
```

#### `POST /api/v1/tasks`
Create a task.

- Body:

| Field | Type | Required | Notes |
|---|---|---|---|
| `title` | string | yes (assumed — confirm against DTO validation annotations) | |
| `description` | string | no | |

- Response: `201 Created` (confirm actual status code against the controller — some CRUD templates return `200`) with the created task, including its generated `id`.
- Errors: `400 Bad Request` on validation failure (see *Conventions* above).

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Write release notes","description":"For this release"}'
```

#### `DELETE /api/v1/tasks/{id}`
Delete a task.

- Path params: `id` (integer, required).
- Response: `204 No Content` (confirm against the controller).
- Errors: `404 Not Found` if `id` doesn't exist.

```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1
```

### Operational

#### `GET /actuator/health`
Liveness/readiness — used by `scripts/healthcheck.sh` and the Docker `HEALTHCHECK`.

- Response: `200 OK` when healthy, e.g. `{"status":"UP"}` (add more detail by setting `management.endpoint.health.show-details` — see [CONFIGURATION.md](CONFIGURATION.md)).

```bash
curl http://localhost:8080/actuator/health
```
