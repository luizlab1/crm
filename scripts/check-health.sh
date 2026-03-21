#!/usr/bin/env bash
set -eo pipefail

BASE_DIR="$HOME/projects/crm"
HEALTH_URL="http://127.0.0.1:8080/actuator/health"
TIMEOUT=${1:-10}

start_time=$(date +%s)
while true; do
  if curl -sS "$HEALTH_URL" >/dev/null 2>&1; then
    echo "ok"
    exit 0
  fi
  if [ $(( $(date +%s) - start_time )) -ge $TIMEOUT ]; then
    echo "timeout"
    exit 1
  fi
  sleep 1
done
