#!/usr/bin/env bash
# Starts java-application-template on Linux/Mac.
set -euo pipefail
cd "$(dirname "$0")/.."

APP_HOME="$(pwd)"
JAR="$(ls "$APP_HOME"/lib/java-application-template*.jar 2>/dev/null | head -n1)"
CONFIG_DIR="$APP_HOME/config"
PID_FILE="$APP_HOME/java-application-template.pid"

if [ -z "$JAR" ]; then
    echo "ERROR: no jar found in $APP_HOME/lib/" >&2
    exit 1
fi

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    echo "java-application-template is already running (PID $(cat "$PID_FILE"))."
    exit 1
fi

echo "Starting java-application-template..."
nohup java -Dlog4j.configurationFile="$CONFIG_DIR/log4j2.xml" \
    -jar "$JAR" \
    --spring.config.additional-location="file:$CONFIG_DIR/" \
    > /dev/null 2>&1 &

echo $! > "$PID_FILE"
echo "Started with PID $(cat "$PID_FILE")."

