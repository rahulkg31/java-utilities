#!/usr/bin/env bash
# Verifies the bare-metal (non-Docker) app is actually up.
set -euo pipefail
PORT="${SERVER_PORT:-8080}"

if curl -fsS "http://localhost:${PORT}/actuator/health" | grep -q '"status":"UP"'; then
    echo "java-application-template is healthy on port ${PORT}."
    exit 0
else
    echo "java-application-template health check FAILED on port ${PORT}."
    exit 1
fi
