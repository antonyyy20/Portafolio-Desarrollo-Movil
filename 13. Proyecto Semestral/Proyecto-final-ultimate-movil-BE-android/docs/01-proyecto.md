# 01 — Descripción del proyecto

> **Grupo:** Quickvnt · **Salón:** 1SF-241

## Información general

| Campo | Valor |
|-------|-------|
| **Nombre del proyecto** | Quickvnt |
| **Grupo** | Quickvnt |
| **Salón** | 1SF-241 |
| **Tipo** | Aplicación móvil Android + API REST |
| **Dominio** | Gestión de eventos MICE |
| **Versión** | 1.0.0 |
| **Repositorio** | https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE |

---

## Arquitectura

![Arquitectura Quickvnt](assets/arquitectura.png)

Detalle técnico en [02-arquitectura.md](./02-arquitectura.md).

---

## Contexto

<!-- TODO: Describir el contexto académico o empresarial -->

Quickvnt surge como respuesta a la necesidad de digitalizar la gestión de eventos corporativos en el sector MICE. Los eventos como conferencias, ferias comerciales, congresos y reuniones de incentivo requieren coordinación entre múltiples actores: organizadores, asistentes y personal de apoyo.

**Grupo:** Quickvnt  
**Salón:** 1SF-241  
**Curso / asignatura:** Desarrollo Móvil

---

## Problema identificado

### Situación actual

1. **Registro fragmentado:** Los asistentes se registran por formularios web, correo o presencialmente sin un sistema unificado.
2. **Control de acceso ineficiente:** Listas impresas y verificación manual en la entrada generan filas y errores.
3. **Falta de visibilidad:** Los organizadores no tienen métricas en tiempo real sobre registros y asistencia.
4. **Experiencia móvil limitada:** No existe una app nativa que integre todo el flujo del evento.

### Impacto

<!-- TODO: Completar con datos o ejemplos concretos de tu investigación -->

- Pérdida de tiempo en check-in
- Errores en conteo de asistentes
- Dificultad para tomar decisiones durante el evento

---

## Objetivo general

Desarrollar una **plataforma móvil integral** que permita a organizadores, asistentes y staff gestionar el ciclo completo de un evento MICE desde un único ecosistema digital.

---

## Objetivos específicos

| # | Objetivo | Estado |
|---|----------|--------|
| 1 | Implementar autenticación segura con JWT y roles | ✅ Completado |
| 2 | Permitir exploración y registro a eventos publicados | ✅ Completado |
| 3 | Generar tickets digitales con código QR | ✅ Completado |
| 4 | Habilitar CRUD de eventos para organizadores | ✅ Completado |
| 5 | Implementar check-in mediante escáner QR | ✅ Completado |
| 6 | Mostrar analytics y KPIs por evento | ✅ Completado |
| 7 | Desplegar backend en la nube (Render) | ✅ Completado |
| 8 | Documentar el proyecto de forma presentable en GitHub | 🔄 En progreso |

---

## Alcance

### Dentro del alcance (MVP)

- App Android nativa (Kotlin + Compose)
- API REST con FastAPI
- Tres roles: ATTENDEE, ORGANIZER, STAFF
- Marketplace de eventos
- Tickets con QR
- Check-in digital
- Dashboard de analytics básico
- Integración con Google Maps en detalle de evento

### Fuera del alcance (futuras versiones)

<!-- TODO: Ajustar según lo que NO implementaron -->

- Pagos in-app / pasarela de pago
- Notificaciones push
- Chat en tiempo real
- Versión iOS
- Modo offline completo
- Integración con calendario externo (Google Calendar, Outlook)

---

## Justificación

<!-- TODO: Explicar por qué es relevante este proyecto -->

La digitalización de eventos MICE es una tendencia creciente post-pandemia. Una solución móvil nativa ofrece:

- **Accesibilidad:** El asistente lleva su ticket en el teléfono
- **Eficiencia:** Check-in en segundos con QR
- **Datos:** El organizador toma decisiones con analytics en vivo
- **Escalabilidad:** Arquitectura cliente-servidor desplegable en la nube

---

## Usuarios objetivo

| Usuario | Necesidad principal |
|---------|---------------------|
| **Asistente** | Encontrar eventos, registrarse y presentar su ticket |
| **Organizador** | Crear eventos, ver métricas, asignar staff |
| **Staff** | Validar entrada de asistentes con QR |

---

## Resultados esperados

Al finalizar el proyecto se entrega:

1. Repositorio único en GitHub con código fuente y documentación
2. App Android funcional conectada a API en producción
3. README presentable con imágenes y video demo
4. Manual de usuario documentado
5. Backend desplegado y accesible

---

## Referencias

<!-- TODO: Agregar bibliografía, artículos, documentación consultada -->

1. [Android Developers — Jetpack Compose](https://developer.android.com/jetpack/compose)
2. [FastAPI Documentation](https://fastapi.tiangolo.com/)
3. <!-- TODO: Referencia adicional -->

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
