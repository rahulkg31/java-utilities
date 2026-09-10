#!/usr/bin/env bash

set -euo pipefail

ARGS=(--spring.config.additional-location="file:/app/config/")

if [ "${RUN_MIGRATIONS_ON_STARTUP:-true}" = "false" ]; then
    ARGS+=(--spring.flyway.enabled=false)
fi

echo "Starting java-application-template (database: ${DATA_DIR:-/app/data}/taskdb)..."
exec java -jar /app/lib/app.jar "${ARGS[@]}"

