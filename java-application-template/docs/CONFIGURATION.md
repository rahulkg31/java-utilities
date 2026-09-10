# Configuration Reference

## How configuration is layered
The app ships with a single `default` profile pre-activated (`config/application-default.yml`) — there's no profile flag to set for normal use. Spring Boot resolves properties in this order, with later sources winning:

`application.yml` (base defaults, if present) → `application-default.yml` (the active profile, ships with the distribution) → external config file (`--spring.config.location=...`) → environment variables (relaxed binding: dots become underscores, all-caps — e.g. `spring.datasource.url` → `SPRING_DATASOURCE_URL`) → command-line args.

In practice: edit `config/application-default.yml` for anything permanent, use environment variables for anything that varies per-deployment (credentials, ports, paths) — that's what Docker and most process managers set anyway.

There are no custom `@ConfigurationProperties` classes in this app yet — every property below is a native Spring Boot / Flyway / springdoc property, so Spring's own reference docs apply if a row here doesn't cover your question in enough depth.

## Database

| Property | Env var override | Default | Description |
|---|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:h2:file:${DATA_DIR:./data}/taskdb` | JDBC URL of the embedded, file-based H2 database — same in every profile. Interpolates `DATA_DIR` (below); override that instead of this URL if you just need to move the data file. |
| `DATA_DIR` | `DATA_DIR` | `./data` | Directory the H2 database file (`taskdb.mv.db`) is stored in. Not a Spring property itself — it's a placeholder substituted into `spring.datasource.url` above, so setting it alone is enough to relocate the database. |
| `spring.datasource.username` | `DB_USER` | `sa` | Database user. |
| `spring.datasource.password` | `DB_PASSWORD` | *(empty)* | Database password. H2 embedded mode has no real network exposure or auth enforcement, so this mainly guards against accidental local access — it is not a substitute for securing the host. |
| `spring.flyway.enabled` | `SPRING_FLYWAY_ENABLED` | `true` | Whether the app applies `db/migration/*.sql` on startup. Set `false` only if migrations are being run out-of-band (e.g. by a DBA) before the app starts. |
| `spring.jpa.hibernate.ddl-auto` | `SPRING_JPA_HIBERNATE_DDL_AUTO` | `validate` | **Should stay `validate` in production** — Flyway owns schema changes. Changing this to `update` or `create` outside local experimentation will let Hibernate silently alter the schema out of step with your migration history. |
| `spring.h2.console.enabled` | `SPRING_H2_CONSOLE_ENABLED` | `false` | Web-based H2 console at `/h2-console`. **Keep disabled outside local troubleshooting** — it's an unauthenticated SQL console over HTTP if left on. |

## Web server

| Property | Env var override | Default | Description |
|---|---|---|---|
| `server.port` | `SERVER_PORT` | `8080` | HTTP port the app listens on. |

## API docs (springdoc / Swagger)

| Property | Env var override | Default | Description |
|---|---|---|---|
| `springdoc.swagger-ui.enabled` | `SPRINGDOC_SWAGGERUI_ENABLED` | `true` | Interactive API docs at `/swagger-ui.html`. Set `false` to disable — some teams turn this off in production while leaving the raw spec (below) on for internal tooling. |
| `springdoc.api-docs.enabled` | `SPRINGDOC_APIDOCS_ENABLED` | `true` | Machine-readable OpenAPI spec at `/v3/api-docs`. See [docs/API.md](API.md) for what consumes this. |

## Actuator / health

| Property | Env var override | Default | Description |
|---|---|---|---|
| `management.endpoint.health.show-details` | `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS` | `never` | Whether `/actuator/health` shows internal details (disk space, DB status, etc.) or just an up/down status. Consider `when-authorized` rather than `always` if you expose this beyond internal health checks. |
| `management.endpoints.web.exposure.include` | `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health` | Which actuator endpoints are reachable over HTTP at all. **Verify this before widening it** — setting it to `*` exposes endpoints like `/actuator/env` and `/actuator/beans`, which can leak configuration and secrets. `scripts/healthcheck.sh` and the Docker `HEALTHCHECK` only need `health` exposed. |

## Logging

| Property | Env var override | Default | Description |
|---|---|---|---|
| `logging.file.path` | `LOGGING_FILE_PATH` | `logs` | Directory rolling log files are written to. |

## Docker-specific variables (`docker/.env`)

These aren't Spring properties — they're read by `docker-compose.yml` and passed through as the environment variables above.

| Variable | Description |
|---|---|
| `APP_PORT` | Host port mapped to the container's `8080` (i.e. the host side of `server.port`). |
| `DB_USER`, `DB_PASSWORD` | Credentials for the embedded H2 database — defaults `sa` / empty work fine for local use. |
| `RUN_MIGRATIONS_ON_STARTUP` | Set to `false` to have the container skip Flyway on boot (maps to `spring.flyway.enabled`). |

## Where to add a new property
1. Add the property (and its default) to `config/application-default.yml`.
2. If it's application-specific rather than a native Spring/Flyway/springdoc property, bind it through a `@ConfigurationProperties` class instead of scattering `@Value` lookups through the code — future custom properties should get their own table above, listed before the framework ones.
3. Add a row to the appropriate table above (or start a new one if it's a new concern), including the environment variable form.
4. If it's security- or data-loss-sensitive (credentials, schema mode, exposed endpoints), call that out explicitly in the description, the way `ddl-auto` and `endpoints.web.exposure.include` are above.
