#!/usr/bin/env bash
set -euo pipefail

COMPOSE="docker compose"

# Ensure backend env exists
if [[ ! -f back/java/.env ]]; then
  echo "[prep] back/java/.env 이 없어 .env.example을 복사합니다."
  if [[ -f back/java/.env.example ]]; then
    cp back/java/.env.example back/java/.env
  else
    echo "JWT_SECRET=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo1Njc4OTA=" >> back/java/.env
    echo "SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/livecommerce" >> back/java/.env
    echo "SPRING_DATASOURCE_USERNAME=user" >> back/java/.env
    echo "SPRING_DATASOURCE_PASSWORD=password" >> back/java/.env
    echo "REDIS_HOST=redis" >> back/java/.env
    echo "REDIS_PORT=6379" >> back/java/.env
    echo "CORS_ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:3000" >> back/java/.env
  fi
fi

echo "[1/5] backend-java 정지 및 제거"
$COMPOSE rm -sf backend-java >/dev/null 2>&1 || true

echo "[2/5] backend-java 강제 재빌드 (no cache)"
DOCKER_BUILDKIT=1 $COMPOSE build --no-cache backend-java

echo "[3/5] 의존 서비스(db, redis) 기동"
$COMPOSE up -d db redis

echo "[4/5] backend-java 재기동"
$COMPOSE up -d --force-recreate backend-java

echo "[5/5] backend-java 로그 팔로우 (Ctrl-C 로 종료)"
$COMPOSE logs -f backend-java
