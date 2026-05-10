# Contributing

Gracias por tu interés en contribuir a esta presentación de Spec-Driven Development. Esta guía resume el flujo de trabajo, las reglas de la rama `main` y los checks que tu PR debe pasar.

## Workflow

La rama `main` está protegida: requiere PR + checks verdes, no permite force-push, deletions ni history no-lineal. Para cualquier cambio:

```bash
# 1. Crear rama desde main actualizado
git checkout main && git pull
git checkout -b feat/mi-cambio

# 2. Editar, commit (Husky correrá markdownlint en .md modificados)
vim es/slides.default.md
git commit -am "docs(es): tweak slide N"

# 3. Push y abrir PR
git push -u origin feat/mi-cambio
gh pr create --fill

# 4. Esperar checks verdes y mergear con squash
gh pr merge --squash --delete-branch

# 5. Sincronizar local
git checkout main && git pull
```

## Convención de commits

Usamos prefijos al estilo Conventional Commits para que el changelog automático del Release sea legible:

| Prefijo | Cuándo usarlo |
|---|---|
| `feat:` | Nuevo contenido (slide, ejemplo, plantilla) |
| `fix:` | Corregir typo, link roto, error en código de ejemplo |
| `docs:` | Cambios en README, CONTRIBUTING, guías |
| `style:` | Formato (espacios, comas, indentación) sin cambios de contenido |
| `refactor:` | Reorganizar contenido sin cambiar el mensaje |
| `ci:` | Cambios en `.github/workflows/` o configuración de CI |
| `chore:` | Mantenimiento (deps, configs, builds) |

Ejemplos:

- `feat(es): add slide on AC traceability`
- `fix(en): correct broken link in guide.md`
- `docs(readme): add deployment URLs`
- `ci: bump actions/checkout to v6`

## Checks requeridos

Todos los PRs deben pasar estos status checks (configurados en branch protection):

| Check | Qué valida |
|---|---|
| `Markdown lint` | `markdownlint` sobre todos los `.md` (reglas en `.markdownlint.json`) |
| `Broken link check` | `lychee` sobre todos los enlaces de los `.md` |
| `Spec structure check` | `scripts/check-specs.sh` valida que los specs tienen secciones obligatorias |
| `Marp build check` | Renderiza los 6 decks (ES/EN × default/gaia/uncover) como smoke test |
| `build_and_deploy` | Build de Firebase; en push a `main` también deploya a producción |

Si alguno falla, revisa el log del workflow correspondiente en la pestaña "Checks" del PR.

## Validar localmente antes de pushear

```bash
# Lint markdown (auto-fix de lo que se pueda)
npm run lint:md:fix

# Check de links rotos (requiere lychee instalado: brew install lychee)
npm run check:links

# Validar estructura de specs
npm run check:specs

# Todo lo anterior de un golpe
npm run check:all

# Smoke build de los 6 decks
npm run build
```

El hook de Husky (`pre-commit`) corre `markdownlint --fix` automáticamente sobre los `.md` que estás comiteando. Para saltarlo puntualmente: `git commit --no-verify` (no abuses).

## Saltarse la protección en emergencias

Si necesitas mergear sin esperar checks (rollback urgente, etc.):

```bash
gh pr merge --squash --delete-branch --admin
```

Requiere rol admin en el org `lg-labs-pentagon`.

## Sobre Dependabot y secrets

GitHub no inyecta los secrets del repo en PRs creados por Dependabot ni desde forks (medida de seguridad para evitar exfiltración). Por eso el step "Deploy preview (PRs)" de `deploy.yml` se salta automáticamente cuando `FIREBASE_SERVICE_ACCOUNT` no está disponible.

Si quieres dar previews de Firebase también a Dependabot, agrega el secret en **Settings → Secrets and variables → Dependabot** (es independiente del scope de Actions, no se hereda).

## Releases

Para crear una nueva versión con artefactos descargables (HTML/PDF/PPTX):

```bash
git checkout main && git pull
git tag -a v1.2.0 -m "Release notes resumidas aquí"
git push origin v1.2.0
```

Esto dispara `release.yml` y crea un GitHub Release automáticamente. También puedes lanzarlo manualmente desde **Actions → Release → Run workflow**.

Sigue [SemVer](https://semver.org/lang/es/):

- **MAJOR** — cambios incompatibles (ej. eliminar un slide o renombrar un tema)
- **MINOR** — contenido nuevo retrocompatible (nuevo idioma, nuevo ejemplo)
- **PATCH** — correcciones (typos, links, formato)
