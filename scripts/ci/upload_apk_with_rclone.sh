#!/usr/bin/env bash
set -euo pipefail

: "${APK_PATH:?APK_PATH is required}"
: "${APK_NAME:?APK_NAME is required}"
: "${GITHUB_SHA:?GITHUB_SHA is required}"
: "${GITHUB_REF_NAME:?GITHUB_REF_NAME is required}"
: "${GITHUB_RUN_ID:?GITHUB_RUN_ID is required}"
: "${GITHUB_REPOSITORY:?GITHUB_REPOSITORY is required}"

RCLONE_REMOTE="${RCLONE_REMOTE:-gdrive}"
RCLONE_DIR="${RCLONE_DIR:-NoveoKotlin}"
META_NAME="${DRIVE_META_NAME:-.noveo-latest-apk.json}"

sha256_value="$(sha256sum "$APK_PATH" | awk '{print $1}')"
meta_remote="${RCLONE_REMOTE}:${RCLONE_DIR}/${META_NAME}"

existing_meta=""
if rclone lsjson "$meta_remote" >/dev/null 2>&1; then
  existing_meta="$(rclone cat "$meta_remote" 2>/dev/null || true)"
fi

existing_hash=""
existing_link=""
existing_name=""
if [ -n "$existing_meta" ]; then
  existing_hash="$(printf '%s' "$existing_meta" | python3 -c 'import json,sys; data=json.load(sys.stdin); print(data.get("sha256", ""))' 2>/dev/null || true)"
  existing_link="$(printf '%s' "$existing_meta" | python3 -c 'import json,sys; data=json.load(sys.stdin); print(data.get("drive_link", ""))' 2>/dev/null || true)"
  existing_name="$(printf '%s' "$existing_meta" | python3 -c 'import json,sys; data=json.load(sys.stdin); print(data.get("file_name", ""))' 2>/dev/null || true)"
fi

if [ "$existing_hash" = "$sha256_value" ] && [ -n "$existing_link" ]; then
  {
    echo "uploaded=false"
    echo "sha256=$sha256_value"
    echo "drive_link=$existing_link"
    echo "file_name=${existing_name:-$APK_NAME}"
  } >> "$GITHUB_OUTPUT"
  exit 0
fi

timestamp="$(date -u +%Y%m%d-%H%M%S)"
short_sha="${GITHUB_SHA:0:7}"
file_name="NoveoKotlin-${GITHUB_REF_NAME}-${short_sha}-${timestamp}.apk"
remote_dir="${RCLONE_REMOTE}:${RCLONE_DIR}"
remote_apk="${remote_dir}/${file_name}"

rclone mkdir "$remote_dir"
rclone copyto "$APK_PATH" "$remote_apk"
drive_link="$(rclone link "$remote_apk")"
run_url="https://github.com/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"

meta_file="$(mktemp)"
cat > "$meta_file" <<EOF
{
  "sha256": "$sha256_value",
  "file_name": "$file_name",
  "drive_link": "$drive_link",
  "commit_sha": "$GITHUB_SHA",
  "branch": "$GITHUB_REF_NAME",
  "run_url": "$run_url",
  "uploaded_at": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
}
EOF

rclone copyto "$meta_file" "$meta_remote"
rm -f "$meta_file"

{
  echo "uploaded=true"
  echo "sha256=$sha256_value"
  echo "drive_link=$drive_link"
  echo "file_name=$file_name"
} >> "$GITHUB_OUTPUT"
