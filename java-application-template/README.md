# java-application-template

A small task-tracking REST API (Task CRUD), with Flyway migrations, Docker packaging, and a customer-ready distribution build (tar.gz/zip).

## Tech stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Bean Validation
- Flyway (schema migrations)
- H2 — embedded, file-based (no external database server needed, in any environment)
- springdoc-openapi (Swagger UI)
- JUnit 5 (starter present; add tests under `src/test/java` as the API grows)
- Docker + docker-compose

## Project layout

```
java-application-template/
├── src/main/java/          # controllers, services, repositories, entities
├── src/main/resources/     # application*.yml, static resources
├── src/test/java/          # unit tests (JUnit 5)
├── db/migration/           # Flyway schema migration scripts
├── docker/                 # Dockerfile(s), compose files, container entrypoint
├── scripts/                # install.sh, healthcheck.sh
└── docs/                   # INSTALL.md, CONFIGURATION.md, API.md
```

The distribution package (see *Building the distribution* below) mirrors this with `bin/`, `lib/`, `config/`, `data/`, and `logs/` in place of the source folders.

## Quick Config

Everything below works as shipped — only touch these if you need to change how the service runs locally.

| Property                                    | Env var                                     | Default                     |
| ------------------------------------------- | ------------------------------------------- | --------------------------- |
| `server.port`                               | `SERVER_PORT`                               | `8080`                      |
| `app.data-dir`                              | `APP_DATA_DIR`                              | `./data` (H2 file location) |
| `server.ssl.enabled`                        | `SERVER_SSL_ENABLED`                        | `false`                     |
| `springdoc.swagger-ui.path`                 | `SPRINGDOC_SWAGGER_UI_PATH`                 | `/swagger-ui.html`          |
| `management.endpoints.web.exposure.include` | `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health,info`               |

See docs/CONFIGURATION.md for every configurable property.

## Running locally

### From source

Requires JDK 17 and Maven 3.9+, with access to Maven Central.

```bash
mvn clean package
java -jar target/java-application-template-<version>-executable.jar
```

Defaults to an embedded, file-based H2 database — data persists to `./data/taskdb.mv.db` (relative to wherever you run the command from) across restarts. API available at `http://localhost:8080`, Swagger UI at `http://localhost:8080/swagger-ui.html`.

### From a pre-built distribution package

```bash
tar -xzf task-manager-service-<version>.tar.gz
cd task-manager-service-<version>
./scripts/install.sh
./bin/start.sh
./scripts/healthcheck.sh
```

`config/application-default.yml` works as-is out of the box — no profile flag to set; edit it only if you want to change the port, database file location, or add TLS. Once running, browse the API at `http://localhost:8080/swagger-ui.html`.

Windows: use `bin\start.bat` / `bin\stop.bat` instead of the `.sh` scripts.

**What's in the package:**

| Path            | Purpose                                                      |
| --------------- | ------------------------------------------------------------ |
| `bin/`          | Start/stop scripts and a systemd unit template               |
| `lib/`          | The application jar                                          |
| `config/`       | The active configuration, ready to run as shipped — edit directly if needed |
| `db/migration/` | Schema migration scripts, for DBA review or automatic Flyway apply |
| `docker/`       | Dockerfile, compose file, and container entrypoint/healthcheck |
| `docs/`         | Full install guide, configuration reference, and API reference |
| `scripts/`      | One-time install script plus day-2 healthcheck/backup helpers |
| `logs/`         | Empty at ship time — populated at runtime                    |
| `data/`         | Empty at ship time — holds the embedded H2 database file once running |

See docs/INSTALL.md for the full install and upgrade guide.

## Running with Docker

The `docker/` folder ships two compose files — one for production, one for local development.

### Production (`docker-compose.yml`)

Builds the image from `docker/Dockerfile` and runs it as-is:

```bash
cd docker
docker compose -f docker-compose.yml up -d
curl http://localhost:8080/actuator/health
```

Logs and data are bind-mounted to fixed paths on the host: `../logs` and `../data`. `docker/.env` ships ready to run — no external database container, nothing to copy or fill in first.

Configurable via environment variables (or `docker/.env`):

| Variable       | Default | Purpose                                  |
| -------------- | ------- | ----------------------------------------- |
| `APP_PORT`     | `8080`  | Host port mapped to the container's `8080` |
| `APP_UID`      | `1000`  | UID the image is built to run as           |
| `APP_GID`      | `1000`  | GID the image is built to run as           |
| `DB_USER`      | `sa`    | H2 database user                           |
| `DB_PASSWORD`  | *(empty)* | H2 database password                     |

### Development (`docker-compose-dev.yml`)

Adds a separate `build` service for compiling the jar in a Maven container (so you don't need a local JDK/Maven install), plus an `app` service built from `docker/Dockerfile.dev` with a couple of extra knobs for local iteration:

```bash
# Compile the jar via a Maven container (caches dependencies in ../.m2-repo)
docker compose -f docker/docker-compose-dev.yml run --rm build

# Build the dev image and start the app
docker compose -f docker/docker-compose-dev.yml up --build app
```

Same environment variables as production apply to the `app` service, plus:

| Variable        | Default    | Purpose                                             |
| --------------- | ---------- | ---------------------------------------------------- |
| `LOGS_DIR`      | `../logs`  | Host path bind-mounted to `/app/logs`                |
| `DATA_DIR_HOST` | `../data`  | Host path bind-mounted to `/app/data`                |

Since `LOGS_DIR` and `DATA_DIR_HOST` are configurable (unlike the fixed paths in the production file), you can point the dev container at a different logs/data location per checkout without editing the compose file.

## Running tests

```bash
mvn test
```

Runs the JUnit 5 unit test suite (controllers/services). No integration tests yet — add them under `src/test/java` alongside the growing API.

## Docs

- docs/INSTALL.md — full install and upgrade guide
- docs/CONFIGURATION.md — every configurable property
- docs/API.md — endpoint reference and sample requests (also live at `/swagger-ui.html`)
