# Installing java-application-service 

## Requirements
- Java 17 or later
- No external database — the app uses an embedded, file-based H2 database in every profile. Nothing else to install or configure.

## Steps

1. Verify the download, then extract the archive (replace `<version>`below with the actual version you downloaded, e.g. `1.0.0`):
   ```
   sha256sum -c java-application-template-<version>.tar.gz.sha256
   tar -xzf java-application-template-<version>.tar.gz
   cd java-application-template-<version>
   ```
   
2. Run the installer to set up local config and directories:
   ```
   ./scripts/install.sh
   ```

3. (Optional) Review `config/application-default.yml` - it works out of the box with an embedded, file-based H2 database stored under `data/`, and
   applies automatically with no profile flag needed (it's Spring Boot's built-in "default" profile). Adjust `DATA_DIR`, the port, or TLS settings only if you need something different from the defaults. See `CONFIGURATION.md` for every available property.
   
4. Review the migration scripts in `db/migration/`. If your DBA policy requires manual review before schema changes are applied, run them
   by hand against the H2 database file (using the H2 console or the`h2` command-line shell) and then set `spring.flyway.enabled: false`
   in `application-default.yml`. Otherwise the app applies them automatically on startup.
   
5. Start the service:
   ```
   ./bin/start.sh
   ```
   On Windows: `bin\start.bat`. No profile flag to remember either way.

6. Verify it's up:
   ```
   ./scripts/healthcheck.sh
   ```
   or open `http://localhost:8080/actuator/health` in a browser. Browse the API interactively at `http://localhost:8080/swagger-ui.html`.
   
7. Stop the service with `./bin/stop.sh` (or `bin\stop.bat` on Windows).

## Running as a system service (Linux)

Copy `bin/java-application-template.service` to `/etc/systemd/system/`, edit the`WorkingDirectory` and `User` fields, then:

```
sudo systemctl daemon-reload
sudo systemctl enable --now java-application-template
```

## Upgrading

1. Stop the running service.
2. Extract the new version to a sibling directory (e.g.`java-application-template-1.1.0/`) - do not overwrite the old one.
3. Copy your edited `config/application-default.yml` and `docker/.env` (if used) into the new version's `config/`/`docker/` directories.
4. Copy the `data/` directory (contains the H2 database file,`taskdb.mv.db`) from the old version into the new version's `data/`
   directory. This is your database - losing it means losing all data.
5. Start the new version and verify with `scripts/healthcheck.sh` before decommissioning the old directory.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| `start.sh` exits immediately, no PID printed | Java not on `PATH`, or port already in use |
| Health check returns `DOWN` with a database error | `data/` directory not writable, or `taskdb.mv.db` is locked by another running instance — H2 file mode only supports one process at a time |
| App starts but `logs/` is empty | `path` not writable - check directory permissions |
| Flyway error on startup about checksum mismatch | A migration file in `db/migration/` was edited after being applied - never edit a shipped migration, add a new one instead |
| `Database may be already in use` on startup | Another instance (or a leftover `.lock.db` file from an unclean shutdown) is holding the H2 file - stop any other running instance and remove stale lock files under `data/` |
