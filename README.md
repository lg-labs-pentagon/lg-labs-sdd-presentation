# Spec-Driven Development — Presentación para Ingenieros Senior

<!--
Badges del repositorio de GitHub Actions y deploys.
-->

[![Deploy](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/deploy.yml/badge.svg)](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/deploy.yml)
[![Validate](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/validate.yml/badge.svg)](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/validate.yml)
[![Release](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/release.yml/badge.svg)](https://github.com/lg-labs-pentagon/lg-labs-sdd-presentation/actions/workflows/release.yml)
[![Hosted on Firebase](https://img.shields.io/badge/hosted_on-Firebase-FFA000?logo=firebase&logoColor=white)](https://firebase.google.com/docs/hosting)
[![Marp](https://img.shields.io/badge/slides-Marp-1d4ed8?logo=markdown&logoColor=white)](https://marp.app/)

Material bilingüe (ES/EN) para una charla de **30 minutos** sobre Spec-Driven Development (SDD), orientada a equipos de desarrollo senior. Incluye guía con speaker notes, presentación en 3 temas Marp para comparar, ejemplos prácticos en Java y TypeScript, y plantillas reutilizables.

## Estructura

```
sdd-presentation/
├── es/                  # Versión en español
│   ├── guia.md          # Guía extendida + speaker notes
│   ├── slides.default.md
│   ├── slides.gaia.md
│   ├── slides.uncover.md
│   └── ejemplos/        # Caso "Crear pedido"
├── en/                  # English version (mirror)
├── templates/           # Spec template (ES/EN) + PR template
└── assets/              # Diagramas Mermaid reutilizables
```

## Caso de ejemplo

Sistema de pedidos de comida. Feature trabajada: **"Crear un pedido validando que todos los ítems pertenezcan al mismo restaurante y que el restaurante esté abierto"**.

## Cómo renderizar las presentaciones (Marp)

### Instalación

```bash
npm install -g @marp-team/marp-cli
```

O usa la extensión **Marp for VS Code** para previsualización en vivo.

### Generar PDF / PPTX / HTML

```bash
# PDF
marp es/slides.default.md --pdf
marp es/slides.gaia.md --pdf
marp es/slides.uncover.md --pdf

# PowerPoint
marp es/slides.default.md --pptx

# HTML standalone
marp es/slides.default.md --html

# Servidor en vivo (recomendado para comparar los 3 temas)
marp -s .
```

## Comparación de los 3 temas Marp

| Tema | Estilo | Mejor para | Contras |
|------|--------|-----------|---------|
| **default** | Limpio, fondo blanco | Salas iluminadas, presentaciones formales, lectura de código | Puede sentirse genérico |
| **gaia** | Oscuro/sepia, tipografía elegante | Tech talks, salas oscuras, alto contraste | Menos formal |
| **uncover** | Minimalista, centrado, mucho aire | Mensajes de impacto, audiencias grandes | Menos denso de info por slide |

**Recomendación**: empieza con `gaia` para tech talk interno; usa `default` si necesitas imprimir handouts; `uncover` si la audiencia es muy grande.

## Cómo usar este material

1. Lee primero `es/guia.md` (o `en/guide.md`) — contiene speaker notes por slide.
2. Previsualiza los 3 decks con `marp -s .` y elige el tema.
3. Adapta los ejemplos (`ejemplos/`) a tu dominio si quieres personalizarlo.
4. Comparte la `templates/spec-template.es.md` con tu equipo después de la sesión.

## Presupuesto de tiempo (30 min)

| Sección | Tiempo |
|---------|--------|
| Hook + objetivos | 2 min |
| Términos base | 4 min |
| Qué es SDD | 4 min |
| Ciclo SDD | 4 min |
| Ejemplo práctico | 8 min |
| Reglas obligatorias | 4 min |
| Anti-patrones | 2 min |
| Adopción + Q&A | 2 min |

## Despliegue en Firebase Hosting

La presentación es 100% estática (HTML + CSS + JS embebido por Marp), perfecta para Firebase Hosting.

### Estructura ya preparada

```
public/
├── index.html        # landing con selector de idioma + tema
├── es/{default,gaia,uncover}.html
└── en/{default,gaia,uncover}.html
firebase.json         # config de hosting
.firebaserc           # project id (editar)
build.sh              # regenera /public desde los .md
```

### Pasos

```bash
# 1. Instalar Firebase CLI (una sola vez)
npm install -g firebase-tools

# 2. Login
firebase login

# 3. Editar .firebaserc y poner tu project id
#    (o ejecutar: firebase use --add)

# 4. (Opcional) Regenerar los HTML si tocaste los slides
./build.sh

# 5. Preview local
firebase emulators:start --only hosting

# 6. Deploy
firebase deploy --only hosting
```

Tu presentación quedará en `https://<project-id>.web.app/`.

### Workflow al editar slides

```bash
./build.sh                          # regenera HTML
firebase deploy --only hosting      # publica
```

### Despliegue automático con GitHub Actions

El repo incluye `.github/workflows/deploy.yml` que:

- En **push a `main`** → build + deploy al canal `live` (URL pública).
- En **pull request** → genera un **preview channel** (URL temporal de 7 días) y comenta el link en el PR.
- En **workflow_dispatch** → deploy manual desde la UI de GitHub Actions.

#### Configuración (una sola vez)

1. **Crea una service account de Firebase**:

   ```bash
   firebase init hosting:github
   ```

   Esto te guía y crea el secret `FIREBASE_SERVICE_ACCOUNT_<PROJECT_ID>` en GitHub automáticamente.

   **O manualmente**:
   - Ve a Firebase Console → ⚙️ Project settings → Service accounts → "Generate new private key".
   - En GitHub: Settings → Secrets and variables → Actions → New repository secret:
     - **Name**: `FIREBASE_SERVICE_ACCOUNT`
     - **Value**: pega el JSON completo.

2. **Define el project ID como variable**:
   - GitHub: Settings → Secrets and variables → Actions → **Variables** tab → New variable:
     - **Name**: `FIREBASE_PROJECT_ID`
     - **Value**: `tu-project-id`

3. **Push a main** y verás el deploy en la pestaña **Actions**.

> Si prefieres hardcodear el project ID, reemplaza `${{ vars.FIREBASE_PROJECT_ID }}` por el ID en el workflow.

### ¿Y los archivos .md, ejemplos y plantillas?

No se publican (no están en `/public/`). Eso es intencional: lo público es la presentación; el código fuente queda en el repo. Si quieres exponer también los ejemplos, copia esas carpetas a `public/` o ajusta `firebase.json` con un `rewrite`.

## Validación automática (CI)

El workflow `.github/workflows/validate.yml` corre en cada push y PR, y valida:

| Job | Qué chequea |
|---|---|
| **Markdown lint** | Estilo y consistencia con `markdownlint-cli` (config en `.markdownlint.json`). |
| **Broken link check** | Links rotos en todos los `.md` con `lychee`. |
| **Spec structure check** | Que las specs SDD reales tengan las secciones obligatorias: `Context`, `Behavior`, `Acceptance Criteria`, `Out of Scope`, `Glossary`. |
| **Marp build check** | Que los 6 decks compilen a HTML sin errores. |

Si un PR rompe alguno, no se mergea. Esto **es SDD aplicado al propio repo de SDD**: las reglas se enforzan automáticamente.

### Ajustar reglas

- Editar reglas de markdown lint → `.markdownlint.json` ([lista de reglas](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md)).
- Modificar secciones obligatorias en specs → editar el job `spec_structure` en `validate.yml`.

## Licencia

Uso interno libre. Adapta a tu organización.

## Setup local de desarrollo

```bash
# Instalar dependencias (incluye Husky para hooks pre-commit)
npm install

# Comandos útiles
npm run build         # Regenera /public/ desde los .md
npm run preview       # Firebase emulator local
npm run lint:md       # markdownlint
npm run lint:md:fix   # autofix
npm run check:specs   # valida estructura de specs
npm run check:all     # lint + specs
npm run deploy        # build + firebase deploy
```

### Pre-commit hooks (Husky)

Al hacer `git commit`:

1. **lint-staged** corre `markdownlint --fix` sobre los `.md` staged.
2. **check-specs.sh** valida que las specs SDD tengan las secciones obligatorias.

Si algo falla, el commit se aborta. Mismas reglas que CI → "fail fast" en local.

Para saltar el hook puntualmente: `git commit --no-verify` (no abuses).

## Dependabot

`.github/dependabot.yml` configura PRs automáticos semanales (lunes) para:

- **GitHub Actions** — mantiene actualizadas las versiones de actions usadas.
- **npm devDependencies** — agrupa updates de Husky, lint-staged, markdownlint.

Los PRs llegan etiquetados con `dependencies` para filtrarlos fácil.

## Releases

El workflow `.github/workflows/release.yml` genera artefactos versionados:

```bash
git tag v1.0.0
git push origin v1.0.0
```

Esto dispara la creación automática de un GitHub Release con 4 archivos adjuntos:

| Artefacto | Contenido |
|---|---|
| `*-html.zip` | 6 decks renderizados a HTML (ES/EN × 3 temas) |
| `*-pdf.zip` | 6 decks en PDF imprimibles |
| `*-pptx.zip` | 6 decks en PowerPoint editables |
| `*-source.zip` | `.md` fuente + ejemplos + plantillas |

También se puede disparar manualmente: **Actions → Release → Run workflow**.

> Útil para: compartir versiones congeladas con otros equipos, presentaciones offline, o archivar la versión exacta usada en una charla.
