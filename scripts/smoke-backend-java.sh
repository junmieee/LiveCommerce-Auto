#!/usr/bin/env bash
set -euo pipefail

API_BASE=${API_BASE:-http://localhost:8081}
FRONT_ORIGIN=${FRONT_ORIGIN:-http://localhost:3000}

fail() { echo "[FAIL] $1" >&2; exit 1; }
pass() { echo "[PASS] $1"; }

echo "[wait] ${API_BASE} 응답 대기 (최대 60초)"
for i in $(seq 1 60); do
  # Actuator가 없을 수 있으니 루트 엔드포인트로 확인
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "${API_BASE}/") || true
  if [[ "$CODE" == "200" ]]; then
    break
  fi
  sleep 1
  if [[ $i -eq 60 ]]; then fail "서버 기동 타임아웃"; fi
done
pass "서버 응답 OK (/ 루트 200)"

echo "[test] CORS 프리플라이트 검사 (OPTIONS /api/users/login)"
HDRS=$(curl -sSI -X OPTIONS "${API_BASE}/api/users/login" \
  -H "Origin: ${FRONT_ORIGIN}" \
  -H "Access-Control-Request-Method: POST") || true
echo "$HDRS" | grep -qi "Access-Control-Allow-Origin" \
  || fail "CORS 헤더 누락"
pass "CORS 프리플라이트 OK"

echo "[test] Validation 400 응답 검사 (잘못된 회원가입 요청)"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "${API_BASE}/api/users/register" \
  -H 'Content-Type: application/json' \
  -d '{"email":"bad","name":"","password":"123","provider":"local"}')
[[ "$CODE" == "400" ]] || fail "Validation 요청이 ${CODE} 반환"
pass "Validation 400 OK"

echo "모든 스모크 테스트 통과"
