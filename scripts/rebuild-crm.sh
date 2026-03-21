#!/usr/bin/env bash
set -euo pipefail

cd "$HOME/projects/crm"

echo "[rebuild-crm] starting at $(date -u +%Y-%m-%dT%H:%M:%SZ)" >> crm.log

# Rotate crm.log: keep last 7 rotated logs
if [ -f crm.log ]; then
  TS="$(date -u +%Y%m%dT%H%M%SZ)"
  mv crm.log "crm.log.$TS" || true
  # remove older than 7
  ls -1tr crm.log.* 2>/dev/null | head -n -7 | xargs -r rm -f -- || true
fi

# 1) Build
if [ ! -x ./gradlew ]; then
  echo "./gradlew not found or not executable" | tee -a crm.log
  exit 1
fi
./gradlew clean build >> crm.log 2>&1

# 2) Pick the runnable jar from build/libs
JAR="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*plain*.jar' | head -n1 || true)"
if [ -z "$JAR" ]; then
  echo "No runnable jar found in build/libs" | tee -a crm.log
  exit 1
fi

echo "[rebuild-crm] using $JAR" >> crm.log

# 3) Copy to crm.jar
cp -- "$JAR" crm.jar

# 4) Stop previous instance (pidfile then graceful, then force)
if [ -f crm.pid ]; then
  PID=$(cat crm.pid 2>/dev/null || true)
  if [ -n "$PID" ] && kill -0 "$PID" 2>/dev/null; then
    echo "[rebuild-crm] stopping pid $PID" >> crm.log
    kill "$PID" || true
    sleep 5
    if kill -0 "$PID" 2>/dev/null; then
      echo "[rebuild-crm] pid $PID did not exit, killing -9" >> crm.log
      kill -9 "$PID" || true
    fi
  fi
  rm -f crm.pid
fi

# Fallback: pgrep for any lingering crm.jar processes
pgrep -f 'java -jar crm.jar' | xargs -r kill -9 || true

# 5) Start and record pid
nohup java -jar crm.jar --server.port=8080 > crm.log 2>&1 &
echo $! > crm.pid

echo "[rebuild-crm] started pid $(cat crm.pid)" >> crm.log

exit 0
