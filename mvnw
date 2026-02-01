#!/usr/bin/env bash
# Small helper wrapper: prefer local mvn if available, otherwise print instructions.
MVN_CMD="mvn"
if command -v "$MVN_CMD" >/dev/null 2>&1; then
  exec "$MVN_CMD" "$@"
else
  echo "Maven no encontrado en PATH."
  echo "Instala Maven o utiliza el wrapper oficial."
  echo "Windows: choco install maven  (o descarga desde https://maven.apache.org/download.cgi)"
  echo "Linux/Mac: apt-get install maven  o brew install maven"
  exit 1
fi

