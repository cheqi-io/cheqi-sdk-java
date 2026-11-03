#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

# Standard JDK/Maven only: no Rust, native Wasmtime or runtime WASM compiler.
mvn -q -DskipTests package dependency:build-classpath -Dmdep.includeScope=runtime \
  -Dmdep.outputFile=target/validation-demo.classpath
validator_sdk_version=$(sed -n "s/^version = '\([^']*\)'$/\1/p" build.gradle)
validator_demo_classpath="target/cheqi-sdk-${validator_sdk_version}.jar:$(cat target/validation-demo.classpath)"
validator_java=java
if [[ -n "${JAVA_HOME:-}" ]]; then validator_java="$JAVA_HOME/bin/java"; fi
"$validator_java" -cp "$validator_demo_classpath" examples/PayloadValidationExample.java \
  "${1:-src/test/resources/validation/receipt-payload.json}"
