#!/usr/bin/env bash
# Regenera todos los HTMLs Marp en /public a partir de los .md fuente.
# Uso: ./build.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

mkdir -p public/es public/en

for lang in es en; do
  for theme in default gaia uncover; do
    echo "Rendering $lang/$theme..."
    npx -y @marp-team/marp-cli@latest \
      "$lang/slides.$theme.md" \
      --html \
      --output "public/$lang/$theme.html"
  done
done

echo "Listo. Archivos en ./public/"
