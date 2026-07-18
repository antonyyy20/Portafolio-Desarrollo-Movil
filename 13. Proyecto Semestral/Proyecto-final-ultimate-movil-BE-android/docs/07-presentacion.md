# 07 — Guía de presentación en GitHub

> **Grupo:** Quickvnt · **Salón:** 1SF-241

Esta guía te ayuda a cumplir el requisito de entrega:

> **Subir el proyecto final a un solo repositorio en GitHub y hacerlo presentable, con imágenes, videos, etc.**

---

## Objetivo

Transformar el repositorio de "solo código" a un **portafolio profesional** que cualquier evaluador pueda entender en 5 minutos.

---

## Plan de acción (paso a paso)

### Paso 1 — Completar datos del equipo (15 min)

Datos del **Grupo Quickvnt · Salón 1SF-241** (ya documentados en README y `docs/`):

| Integrante | Cédula |
|------------|--------|
| Fong, Enrique | 4-829-300 |
| González, Jabneel | 8-990-229 |
| Guillén, Manuel | 8-1016-1618 |
| Lu, Joaquín | 8-1024-2466 |
| Santimateo, Diego | 9-764-2382 |
| Pimentel, David | 8-1010-750 |

Archivos donde aparece el equipo: `README.md`, `docs/01-proyecto.md` y el resto de documentos en `docs/`.

### Paso 2 — Tomar screenshots (30 min)

```bash
# 1. Ejecuta la app en emulador Pixel 6
# 2. Navega por cada pantalla
# 3. Captura con Ctrl+S o botón cámara del emulador
# 4. Guarda en docs/assets/screenshots/ con la convención de nombres
```

**Mínimo requerido:**

1. `01-splash.png`
2. `03-login.png`
3. `05-marketplace.png`
4. `06-event-detail.png`
5. `07-my-tickets.png`
6. `08-ticket-qr.png`
7. `09-my-events.png`
8. `11-analytics.png`
9. `12-qr-scanner.png`
10. `13-profile.png`

### Paso 3 — Grabar video demo (45 min)

1. Prepara datos de demo (eventos, usuarios)
2. Escribe un guion breve (ver `docs/06-galeria.md`)
3. Graba con OBS o grabación de emulador
4. Exporta como WebM o MP4 en `docs/assets/videos/` (`inicio.webm`, `atendee.webm`, `organizer.webm`)
5. Verifica que los reproductores se ven en README y `docs/06-galeria.md`

### Paso 4 — Diagrama de arquitectura

El diagrama principal está en `docs/assets/arquitectura.png` y debe verse en el README y en `docs/02-arquitectura.md`.

### Paso 5 — Subir todo a GitHub (15 min)

```bash
git add .
git commit -m "docs: documentación completa para presentación del proyecto final"
git push origin TU_RAMA
```

### Paso 6 — Verificación final (10 min)

Abre el repositorio en GitHub y confirma que el README se ve bien formateado, las imágenes cargan, los videos son reproducibles, los enlaces a `docs/` funcionan y un evaluador externo puede clonar y ejecutar el proyecto siguiendo las instrucciones.

---

## Tips para un README profesional

### Buenas prácticas

- Primera línea: qué es el proyecto en una frase
- Badges de tecnologías al inicio
- GIF o video demo arriba del fold
- Tabla de funcionalidades clara
- Instrucciones de instalación en menos de 5 pasos
- Screenshots en fila horizontal
- Links a documentación detallada

### Evitar

- README de una sola línea ("mi proyecto")
- Sin imágenes ni demos
- Instrucciones que no funcionan
- Código sin explicación
- Repositorio con archivos `.env` o claves expuestas
- Múltiples repos para el mismo proyecto

---

## Ejemplo de estructura final en GitHub

```text
Proyecto-final-ultimate-movil-BE/
├── README.md                 ← Portada presentable
├── docs/
│   ├── README.md             ← Índice de documentación
│   ├── 01-proyecto.md
│   ├── 02-arquitectura.md
│   ├── 03-instalacion.md
│   ├── 04-manual-usuario.md
│   ├── 05-api-integracion.md
│   ├── 06-galeria.md
│   ├── 07-presentacion.md    ← Esta guía
│   └── assets/
│       ├── arquitectura.png  ← Diagrama de arquitectura
│       ├── videos/           ← Demos WebM
│       ├── diagrams/         ← Otros diagramas
│       └── logo/             ← Branding
├── app/                      ← Código Android
├── build.gradle.kts
└── ...
```

---

## Criterios de evaluación sugeridos

| Criterio | Peso | Estado |
|----------|------|--------|
| Funcionalidad de la app | 30% | Pendiente |
| Calidad del código | 20% | Pendiente |
| Documentación README | 20% | Pendiente |
| Material visual (imgs/video) | 15% | Pendiente |
| Repositorio organizado | 10% | Pendiente |
| Despliegue / demo en vivo | 5% | Pendiente |

---

## Recursos útiles

| Recurso | URL |
|---------|-----|
| Shields.io (badges) | https://shields.io |
| draw.io (diagramas) | https://app.diagrams.net |
| OBS Studio (grabar) | https://obsproject.com |
| GitHub Docs (markdown) | https://docs.github.com/en/get-started/writing-on-github |
| Mermaid Live | https://mermaid.live |

---

## Contacto para dudas

**Grupo:** Quickvnt · **Salón:** 1SF-241

| Integrante | Cédula |
|------------|--------|
| Fong, Enrique | 4-829-300 |
| González, Jabneel | 8-990-229 |
| Guillén, Manuel | 8-1016-1618 |
| Lu, Joaquín | 8-1024-2466 |
| Santimateo, Diego | 9-764-2382 |
| Pimentel, David | 8-1010-750 |
