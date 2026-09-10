#!/usr/bin/env bash
# Stops java-application-template started via start.sh.
set -euo pipefail
cd "$(dirname "$0")/.."
PID_FILE="$(pwd)/java-application-template.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "No PID file found — is the service running?"
    exit 1
fi

PID="$(cat "$PID_FILE")"
if kill -0 "$PID" 2>/dev/null; then
    echo "Stopping java-application-template (PID $PID)..."
    kill "$PID"
    rm -f "$PID_FILE"
    echo "Stopped."
else
    echo "Process $PID not running. Removing stale PID file."
    rm -f "$PID_FILE"
fi
