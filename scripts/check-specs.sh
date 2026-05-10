#!/usr/bin/env bash
# Verifica que los archivos de spec SDD reales tengan las secciones obligatorias.
# Mismas reglas que el job spec_structure de CI.
set -e

REQUIRED=(
  "## 1. Context"
  "## 2. Behavior"
  "## 3. Acceptance Criteria"
  "## 4. Out of Scope"
  "## 5. Glossary"
)

fail=0

check() {
  local file="$1"
  shift
  for section in "$@"; do
    if ! grep -qF "$section" "$file"; then
      echo "ERROR: $file is missing required section: $section"
      fail=1
    fi
  done
}

# Specs reales en ambos idiomas
for f in es/ejemplos/01-spec-*.md en/examples/01-spec-*.md; do
  [ -f "$f" ] && check "$f" "${REQUIRED[@]}"
done

# Plantillas
for f in templates/spec-template.*.md; do
  [ -f "$f" ] && check "$f" \
    "## 1. Context" \
    "## 2. Behavior" \
    "## 3. Acceptance Criteria"
done

if [ $fail -ne 0 ]; then
  echo "Spec structure check FAILED"
  exit 1
fi

echo "Spec structure check OK"
