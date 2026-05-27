#!/bin/sh
set -eu

if [ "YES" = "${OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED:-}" ]; then
  echo 'Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES'
  exit 0
fi

if [ "${PLATFORM_NAME:-}" = "iphonesimulator" ]; then
  if [ -z "${ARCHS:-}" ] || [ "$ARCHS" = "x86_64" ]; then
    echo "Normalizing simulator ARCHS to arm64 for Compose Multiplatform"
    export ARCHS=arm64
  fi
  if [ -z "${CURRENT_ARCH:-}" ] || [ "$CURRENT_ARCH" = "undefined_arch" ] || [ "$CURRENT_ARCH" = "x86_64" ]; then
    export CURRENT_ARCH=arm64
  fi
  if [ -z "${NATIVE_ARCH_ACTUAL:-}" ] || [ "$NATIVE_ARCH_ACTUAL" = "undefined_arch" ] || [ "$NATIVE_ARCH_ACTUAL" = "x86_64" ]; then
    export NATIVE_ARCH_ACTUAL=arm64
  fi
fi

PROJECT_ROOT="$(cd "${SRCROOT}/.." && pwd -P)"
cd "$PROJECT_ROOT"

# Keep Gradle caches inside the project so Xcode can read/write (avoids ~/Documents + ~/.gradle TCC issues).
export GRADLE_USER_HOME="${PROJECT_ROOT}/.gradle"
mkdir -p "$GRADLE_USER_HOME"

# Clear quarantine flags that can block Xcode from reading wrapper files.
if command -v xattr >/dev/null 2>&1; then
  xattr -cr "${PROJECT_ROOT}/gradle" 2>/dev/null || true
  xattr -cr "${PROJECT_ROOT}/gradlew" 2>/dev/null || true
fi

if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
  JAVACMD="${JAVA_HOME}/bin/java"
elif JAVA_HOME_PATH="$(/usr/libexec/java_home 2>/dev/null)" && [ -n "$JAVA_HOME_PATH" ] && [ -x "${JAVA_HOME_PATH}/bin/java" ]; then
  JAVACMD="${JAVA_HOME_PATH}/bin/java"
elif command -v java >/dev/null 2>&1; then
  JAVACMD=java
else
  echo "error: Java not found. Install a JDK (17+) and set JAVA_HOME in Xcode." >&2
  exit 1
fi

WRAPPER_JAR="${PROJECT_ROOT}/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="${PROJECT_ROOT}/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "error: Gradle wrapper JAR not found at ${WRAPPER_JAR}" >&2
  echo "Run './gradlew --version' once from the project root in Terminal to restore it." >&2
  exit 1
fi

# Xcode builds from ~/Documents often cannot read the wrapper JAR directly ("Unable to access jarfile").
# Copy it to TMPDIR (allowed) before running Java.
GRADLE_JAR_TO_RUN="$WRAPPER_JAR"
SHOULD_STAGE=false
if [ -n "${XCODE_VERSION_ACTUAL:-}" ]; then
  SHOULD_STAGE=true
elif ! cat "$WRAPPER_JAR" > /dev/null 2>&1; then
  SHOULD_STAGE=true
fi

if [ "$SHOULD_STAGE" = true ]; then
  STAGING_DIR="${TMPDIR%/}/anime-movies-gradle-wrapper"
  mkdir -p "${STAGING_DIR}/gradle/wrapper"
  cp -f "$WRAPPER_JAR" "${STAGING_DIR}/gradle/wrapper/gradle-wrapper.jar"
  if [ -f "$WRAPPER_PROPERTIES" ]; then
    cp -f "$WRAPPER_PROPERTIES" "${STAGING_DIR}/gradle/wrapper/gradle-wrapper.properties"
  fi
  GRADLE_JAR_TO_RUN="${STAGING_DIR}/gradle/wrapper/gradle-wrapper.jar"
  echo "Using staged Gradle wrapper at ${GRADLE_JAR_TO_RUN}"
fi

echo "Running Gradle from ${PROJECT_ROOT}"
exec "$JAVACMD" \
  -Dorg.gradle.appname=gradlew \
  -Dgradle.user.home="${GRADLE_USER_HOME}" \
  -jar "$GRADLE_JAR_TO_RUN" \
  :composeApp:embedAndSignAppleFrameworkForXcode
