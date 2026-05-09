#!/usr/bin/env bash
set -euo pipefail

SDK_ROOT="${1:-/workspace/android-sdk}"
TOOLS_ZIP="${SDK_ROOT}/cmdline-tools.zip"
TOOLS_DIR="${SDK_ROOT}/cmdline-tools"

mkdir -p "$TOOLS_DIR"

if [ ! -x "$TOOLS_DIR/latest/bin/sdkmanager" ]; then
  curl -L "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" -o "$TOOLS_ZIP"
  unzip -qo "$TOOLS_ZIP" -d "$TOOLS_DIR"
  rm -rf "$TOOLS_DIR/latest"
  mv "$TOOLS_DIR/cmdline-tools" "$TOOLS_DIR/latest"
fi

set +e
yes | "$TOOLS_DIR/latest/bin/sdkmanager" --sdk_root="$SDK_ROOT" \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0" >/dev/null
SDK_EXIT=$?
set -e
if [ "$SDK_EXIT" -ne 0 ] && [ "$SDK_EXIT" -ne 141 ]; then
  exit "$SDK_EXIT"
fi

cat > "$(dirname "$0")/../local.properties" <<PROP
sdk.dir=${SDK_ROOT}
PROP

echo "SDK ready at ${SDK_ROOT}"
echo "local.properties written to android/local.properties"
