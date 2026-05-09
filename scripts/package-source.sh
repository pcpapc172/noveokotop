#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OUT_DIR="$ROOT_DIR/artifacts"
OUT_FILE="$OUT_DIR/noveo-android-client-source.zip"

mkdir -p "$OUT_DIR"
rm -f "$OUT_FILE"

cd "$ROOT_DIR"
zip -r -9 "$OUT_FILE" android \
  -x "android/.gradle/*" \
  -x "android/**/build/*" \
  -x "android/local.properties" \
  -x "android/**/.DS_Store" \
  -x "android/**/captures/*" \
  -x "android/.idea/*" >/dev/null

echo "$OUT_FILE"
