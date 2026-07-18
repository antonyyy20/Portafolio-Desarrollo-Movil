# Assets del proyecto — Guía de medios

> **Grupo:** Quickvnt · **Salón:** 1SF-241

Coloca aquí todas las imágenes, videos y diagramas que se referencian en la documentación.

## Estructura de carpetas

```text
docs/assets/
├── screenshots/
│   ├── pantalla_de_inicio.png
│   ├── inicio_de_sesion.png
│   ├── registro.png
│   ├── perfil.png
│   ├── atendee/          # Flujo asistente
│   ├── organizer/        # Flujo organizador
│   └── Staff/            # Flujo staff
├── videos/               # Videos de demostración (.mp4, .webm)
├── arquitectura.png      # Diagrama de arquitectura completa
├── diagrams/             # Otros diagramas (flujos, ER, etc.)
├── team/                 # Fotos del equipo (opcional)
└── logo/                 # Logo del proyecto, banner, icono
```

## Capturas por carpeta

La galería en [06-galeria.md](../06-galeria.md) sigue esta estructura:

| Carpeta | Contenido |
|---------|-----------|
| `screenshots/` (raíz) | Onboarding, login, registro, perfil |
| `screenshots/atendee/` | Marketplace, detalle, boletos, QR |
| `screenshots/organizer/` | Panel, crear evento, analytics, staff, QR |
| `screenshots/Staff/` | Pantalla de staff |

## Videos

| Archivo | Contenido |
|---------|-----------|
| `inicio.webm` | Pantalla de bienvenida, login y registro |
| `atendee.webm` | Flujo del rol ATTENDEE (marketplace, tickets, QR) |
| `organizer.webm` | Flujo del rol ORGANIZER (eventos, analytics, staff, QR) |

Ver reproductores embebidos en [06-galeria.md](../06-galeria.md).

## Diagramas

| Archivo | Contenido |
|---------|-----------|
| `arquitectura.png` | Arquitectura completa: Android + FastAPI + PostgreSQL |
| `diagrams/flujo-usuario.png` | Flujo por roles |
| `diagrams/modelo-datos.png` | Entidades principales |

## Recomendaciones técnicas

- **Screenshots:** PNG o WebP, resolución mínima 1080×1920 (vertical).
- **Videos:** WebM o MP4 (H.264). Los demos actuales están en `videos/*.webm`.
- **Diagramas:** PNG o SVG. Herramientas sugeridas: draw.io, Figma, Excalidraw, Mermaid.

Desde archivos en `docs/` (ej. `06-galeria.md`, `04-manual-usuario.md`):

```markdown
![Marketplace](assets/screenshots/atendee/marketplace.png)
```

Desde el `README.md` en la raíz del repo:

```markdown
![Arquitectura Quickvnt](./docs/assets/arquitectura.png)

<video src="./docs/assets/videos/atendee.webm" controls width="600"></video>
```

**Importante:** No uses `../assets/` desde archivos dentro de `docs/` — esa ruta apunta fuera de la carpeta y GitHub no encuentra la imagen.

---

## Equipo — Quickvnt · Salón 1SF-241

| Integrante | Cédula |
|------------|--------|
| Fong, Enrique | 4-829-300 |
| González, Jabneel | 8-990-229 |
| Guillén, Manuel | 8-1016-1618 |
| Lu, Joaquín | 8-1024-2466 |
| Santimateo, Diego | 9-764-2382 |
| Pimentel, David | 8-1010-750 |
