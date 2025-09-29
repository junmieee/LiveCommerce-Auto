#!/usr/bin/env sh
set -e

ROOT_DIR="$(pwd)"
HOOK_SRC="${ROOT_DIR}/githooks/pre-commit"
HOOK_DST="${ROOT_DIR}/.git/hooks/pre-commit"

if [ ! -d "${ROOT_DIR}/.git" ]; then
  echo "[install-git-hooks] Not a git repository; skipping."
  exit 0
fi

if [ ! -f "${HOOK_SRC}" ]; then
  echo "[install-git-hooks] Hook source not found: ${HOOK_SRC}"
  exit 1
fi

mkdir -p "${ROOT_DIR}/.git/hooks"
cp "${HOOK_SRC}" "${HOOK_DST}"
chmod +x "${HOOK_DST}"

# Ensure this repo uses its local hooks path even if a global core.hooksPath is set
git config --local core.hooksPath .git/hooks || true

echo "[install-git-hooks] Installed pre-commit hook -> .git/hooks/pre-commit"
