#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

# fisher.sh - production-ready launcher for the Spring Boot app (Maven Wrapper + PostgreSQL)
# - Waits for PostgreSQL via pg_isready
# - Runs Checkstyle, SpotBugs, tests, packaging
# - Detects generated JAR in target/ and runs it
# - Supports --stop, --dry-run, --spring-profile, health polling

# ──────────────────────────────────────────────
# Defaults
# ──────────────────────────────────────────────
DB_HOST_DEFAULT="localhost"
DB_PORT_DEFAULT="5432"
DB_USER_DEFAULT="postgres"
DB_NAME_DEFAULT="postgres"
WAIT_TIMEOUT_DEFAULT=60
APP_PORT_DEFAULT=8080
HEALTH_TIMEOUT_DEFAULT=60
SPRING_PROFILE_DEFAULT=""

DB_HOST="${DB_HOST:-$DB_HOST_DEFAULT}"
DB_PORT="${DB_PORT:-$DB_PORT_DEFAULT}"
DB_USER="${DB_USER:-$DB_USER_DEFAULT}"
DB_NAME="${DB_NAME:-$DB_NAME_DEFAULT}"
DB_PASSWORD="${DB_PASSWORD:-}"            # read from env only — never hardcoded
WAIT_TIMEOUT="${WAIT_TIMEOUT:-$WAIT_TIMEOUT_DEFAULT}"
PG_ISREADY_CMD="${PG_ISREADY_CMD:-pg_isready}"
APP_PORT="${APP_PORT:-$APP_PORT_DEFAULT}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-$HEALTH_TIMEOUT_DEFAULT}"
SPRING_PROFILE="${SPRING_PROFILE:-$SPRING_PROFILE_DEFAULT}"
SKIP_CHECKSTYLE_DEFAULT=false
SKIP_CHECKSTYLE="${SKIP_CHECKSTYLE:-$SKIP_CHECKSTYLE_DEFAULT}"

DRY_RUN=false
STOP_MODE=false

# Directories and files
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
PID_FILE="$LOG_DIR/app.pid"
LOG_FILE="$LOG_DIR/app.log"

# ──────────────────────────────────────────────
# Maven command detection (search up the tree for mvnw)
# ──────────────────────────────────────────────
find_mvnw_up() {
  search_dir="$PWD"
  while [ -n "$search_dir" ] && [ "$search_dir" != "/" ]; do
    if [ -x "$search_dir/mvnw" ]; then
      printf "%s" "$search_dir/mvnw"
      return 0
    fi
    if [ -f "$search_dir/mvnw" ]; then
      printf "%s" "$search_dir/mvnw"
      return 0
    fi
    if [ -f "$search_dir/mvnw.cmd" ]; then
      printf "%s" "$search_dir/mvnw.cmd"
      return 0
    fi
    search_dir=$(dirname "$search_dir")
  done
  return 1
}

mvnw_path=$(find_mvnw_up || true)
if [ -n "$mvnw_path" ]; then
  MVN_CMD="$mvnw_path"
  PROJECT_ROOT="$(cd "$(dirname "$mvnw_path")" && pwd)"
else
  # fallback: search upward from script dir for pom.xml to find project root
  search_dir="$SCRIPT_DIR"
  PROJECT_ROOT=""
  while [ -n "$search_dir" ] && [ "$search_dir" != "/" ]; do
    if [ -f "$search_dir/pom.xml" ]; then
      PROJECT_ROOT="$search_dir"
      break
    fi
    search_dir=$(dirname "$search_dir")
  done
  if [ -n "$PROJECT_ROOT" ] && command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
  else
    printf "[ERROR] Maven wrapper or mvn not found. Run this script from the project tree or install Maven.\n" >&2
    exit 1
  fi
fi

# Ensure logs directory exists (use absolute path)
mkdir -p "$LOG_DIR"

# Move to project root for Maven operations so pom.xml is found
if [ -n "$PROJECT_ROOT" ]; then
  cd "$PROJECT_ROOT"
fi

# ──────────────────────────────────────────────
# Helpers
# ──────────────────────────────────────────────
log_section() { printf "\n========== %s %s==========\n" "$1" "${2:-}"; }
echo_info()   { printf "[INFO]  %s\n" "$1"; }
echo_warn()   { printf "[WARN]  %s\n" "$1" >&2; }
echo_error()  { printf "[ERROR] %s\n" "$1" >&2; }

run() {
  if $DRY_RUN; then
    echo_info "[DRY-RUN] would run: $*"
  else
    "$@"
  fi
}

usage() {
  cat <<EOF
Usage: $0 [OPTIONS]

Options:
  --db-host HOST         PostgreSQL host            (default: $DB_HOST_DEFAULT)
  --db-port PORT         PostgreSQL port            (default: $DB_PORT_DEFAULT)
  --db-user USER         PostgreSQL user            (default: $DB_USER_DEFAULT)
  --db-name NAME         PostgreSQL database name   (default: $DB_NAME_DEFAULT)
  --timeout SECONDS      DB wait timeout            (default: $WAIT_TIMEOUT_DEFAULT)
  --app-port PORT        App port for health check  (default: $APP_PORT_DEFAULT)
  --health-timeout SECS  Health poll timeout        (default: $HEALTH_TIMEOUT_DEFAULT)
  --spring-profile NAME  Spring active profile      (e.g. dev, prod)
  --stop                 Stop a running instance via PID file
  --dry-run              Print actions without executing
  -h, --help             Show this help

Environment variables respected:
  DB_HOST, DB_PORT, DB_USER, DB_NAME, DB_PASSWORD,
  WAIT_TIMEOUT, PG_ISREADY_CMD, APP_PORT, HEALTH_TIMEOUT,
  SPRING_PROFILE
EOF
  exit 0
}

# ──────────────────────────────────────────────
# Argument parsing
# ──────────────────────────────────────────────
while [ "$#" -gt 0 ]; do
  case "$1" in
    --db-host)         DB_HOST="$2";         shift 2 ;;
    --db-port)         DB_PORT="$2";         shift 2 ;;
    --db-user)         DB_USER="$2";         shift 2 ;;
    --db-name)         DB_NAME="$2";         shift 2 ;;
    --timeout)         WAIT_TIMEOUT="$2";    shift 2 ;;
    --app-port)        APP_PORT="$2";        shift 2 ;;
    --health-timeout)  HEALTH_TIMEOUT="$2";  shift 2 ;;
    --spring-profile)  SPRING_PROFILE="$2";  shift 2 ;;
    --skip-checkstyle)  SKIP_CHECKSTYLE=true;        shift ;;
    --stop)            STOP_MODE=true;       shift ;;
    --dry-run)         DRY_RUN=true;         shift ;;
    -h|--help)         usage ;;
    --)                shift; break ;;
    *) echo_error "Unknown argument: $1"; usage ;;
  esac
done

# ──────────────────────────────────────────────
# --stop mode: kill running instance
# ──────────────────────────────────────────────
if $STOP_MODE; then
  if [ ! -f "$PID_FILE" ]; then
    echo_error "No PID file found at $PID_FILE. Is the app running?"
    exit 1
  fi
  pid=$(cat "$PID_FILE")
  if kill -0 "$pid" >/dev/null 2>&1; then
    echo_info "Stopping application (PID: $pid)..."
    kill "$pid"
    rm -f "$PID_FILE"
    echo_info "Application stopped."
  else
    echo_warn "PID $pid is not running. Removing stale PID file."
    rm -f "$PID_FILE"
  fi
  exit 0
fi

# ──────────────────────────────────────────────
# Configuration summary
# ──────────────────────────────────────────────
log_section "Configuration"
echo_info "DB Host:        $DB_HOST"
echo_info "DB Port:        $DB_PORT"
echo_info "DB User:        $DB_USER"
echo_info "DB Name:        $DB_NAME"
echo_info "DB Password:    ${DB_PASSWORD:+(set via env)}"
echo_info "Maven command:  $MVN_CMD"
echo_info "Spring profile: ${SPRING_PROFILE:-(none)}"
echo_info "App port:       $APP_PORT"
$DRY_RUN && echo_info "*** DRY-RUN MODE — no commands will execute ***"

# ──────────────────────────────────────────────
# pg_isready availability check
# ──────────────────────────────────────────────
if ! command -v "${PG_ISREADY_CMD%% *}" >/dev/null 2>&1; then
  echo_error "pg_isready not found (checked: $PG_ISREADY_CMD). Install PostgreSQL client tools."
  exit 1
fi

# ──────────────────────────────────────────────
# Wait for PostgreSQL
# ──────────────────────────────────────────────
log_section "Waiting for PostgreSQL" "(timeout: ${WAIT_TIMEOUT}s)"
echo_info "Probing $DB_HOST:$DB_PORT as $DB_USER ..."
start_time=$(date +%s)
while true; do
  if $DRY_RUN || $PG_ISREADY_CMD -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1; then
    echo_info "PostgreSQL is available."
    break
  fi
  now=$(date +%s)
  elapsed=$((now - start_time))
  if [ "$elapsed" -ge "$WAIT_TIMEOUT" ]; then
    echo_error "Timed out waiting for PostgreSQL after ${WAIT_TIMEOUT}s"
    exit 1
  fi
  printf '.'
  sleep 2
done

# ──────────────────────────────────────────────
# Checkstyle (can be skipped with SKIP_CHECKSTYLE env or --skip-checkstyle)
# ──────────────────────────────────────────────
if [ "$SKIP_CHECKSTYLE" = "true" ] || [ "$SKIP_CHECKSTYLE" = "1" ]; then
  log_section "Checkstyle"
  echo_info "SKIP_CHECKSTYLE is set — skipping Checkstyle step"
else
  log_section "Checkstyle"
  echo_info "Running Checkstyle via Maven"
  run "$MVN_CMD" checkstyle:check
fi

# ──────────────────────────────────────────────
# SpotBugs  (skip gracefully if plugin not configured)
# ──────────────────────────────────────────────
log_section "SpotBugs"
if grep -q "spotbugs-maven-plugin" "$PROJECT_ROOT/pom.xml" 2>/dev/null; then
  echo_info "Running SpotBugs static analysis"
  run "$MVN_CMD" spotbugs:check
else
  echo_warn "SpotBugs plugin not found in pom.xml — skipping. Add com.github.spotbugs:spotbugs-maven-plugin to pom.xml to enable."
fi

# ──────────────────────────────────────────────
# Unit Tests
# ──────────────────────────────────────────────
log_section "Unit Tests"
echo_info "Running unit tests"
run "$MVN_CMD" test

# ──────────────────────────────────────────────
# Packaging
# ──────────────────────────────────────────────
log_section "Packaging"
echo_info "Packaging application (skip tests — already ran above)"
run "$MVN_CMD" package -DskipTests

# ──────────────────────────────────────────────
# JAR detection
# ──────────────────────────────────────────────
log_section "Detecting JAR"
mkdir -p "$LOG_DIR"

if $DRY_RUN; then
  jar_file="target/app-SNAPSHOT.jar"
  echo_info "[DRY-RUN] would use JAR: $jar_file"
else
  jar_file=$(ls -t target/*.jar 2>/dev/null \
    | grep -vE '\.original$|-sources\.jar|-javadoc\.jar' \
    | head -n1 || true)
  if [ -z "$jar_file" ]; then
    echo_error "No JAR found in target/. Built artifacts:"
    ls -la target || true
    exit 1
  fi
  echo_info "Found JAR: $jar_file"
fi

# ──────────────────────────────────────────────
# Build java command
# ──────────────────────────────────────────────
JAVA_OPTS_EXTRA=""
[ -n "$SPRING_PROFILE" ] && JAVA_OPTS_EXTRA="$JAVA_OPTS_EXTRA -Dspring.profiles.active=$SPRING_PROFILE"
[ -n "$DB_PASSWORD" ]    && JAVA_OPTS_EXTRA="$JAVA_OPTS_EXTRA -DSPRING_DATASOURCE_PASSWORD=$DB_PASSWORD"

# ──────────────────────────────────────────────
# Rotate / truncate old log
# ──────────────────────────────────────────────
if [ -f "$LOG_FILE" ] && ! $DRY_RUN; then
  rotated="$LOG_DIR/app-$(date +%Y%m%d-%H%M%S).log"
  mv "$LOG_FILE" "$rotated"
  echo_info "Rotated previous log -> $rotated"
fi

# ──────────────────────────────────────────────
# Launch application
# ──────────────────────────────────────────────
log_section "Starting Application"
echo_info "Launching JAR in background -> $LOG_FILE"

if $DRY_RUN; then
  echo_info "[DRY-RUN] would run: nohup java $JAVA_OPTS_EXTRA -jar $jar_file > $LOG_FILE 2>&1 &"
else
  # shellcheck disable=SC2086
  nohup java $JAVA_OPTS_EXTRA -jar "$jar_file" > "$LOG_FILE" 2>&1 &
  pid=$!
  echo "$pid" > "$PID_FILE"
  echo_info "Application started (PID: $pid). PID saved to $PID_FILE"
fi

# ──────────────────────────────────────────────
# Health polling (actuator/health)
# ──────────────────────────────────────────────
log_section "Health Check" "(timeout: ${HEALTH_TIMEOUT}s)"
if $DRY_RUN; then
  echo_info "[DRY-RUN] would poll http://localhost:${APP_PORT}/actuator/health"
elif ! command -v curl >/dev/null 2>&1; then
  echo_warn "curl not found — skipping health check. Tail logs: tail -f $LOG_FILE"
else
  echo_info "Polling http://localhost:${APP_PORT}/actuator/health ..."
  health_start=$(date +%s)
  healthy=false
  while true; do
    if curl -sf "http://localhost:${APP_PORT}/actuator/health" | grep -q '"status":"UP"' 2>/dev/null; then
      healthy=true
      break
    fi
    now=$(date +%s)
    elapsed=$((now - health_start))
    if [ "$elapsed" -ge "$HEALTH_TIMEOUT" ]; then
      break
    fi
    printf '.'
    sleep 2
  done
  echo  # newline after dots

  if $healthy; then
    echo_info "Application is healthy ✓"
  else
    echo_warn "Health check did not return UP within ${HEALTH_TIMEOUT}s."
    echo_warn "App may still be starting. Check: tail -f $LOG_FILE"
    echo_warn "To stop: $0 --stop"
    # Not exiting with error — app process is running; health endpoint may not be configured
  fi
fi

log_section "Done"
echo_info "Tail logs : tail -f $LOG_FILE"
echo_info "Stop app  : $0 --stop"
echo_info "script completed successfully."

exit 0