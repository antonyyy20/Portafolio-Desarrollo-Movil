# Arquitectura del Sistema Quickvnt

## 1. Visión general

Quickvnt es un **MVP SaaS** para la gestión integral de eventos **MICE** (*Meetings, Incentives, Conferences and Exhibitions*). El sistema se construye como un **monolito modular**: una sola aplicación backend desplegable, organizada internamente en módulos con responsabilidades separadas.

La app móvil Android (Kotlin + Jetpack Compose) es el cliente principal. El backend en **Python/FastAPI** centraliza la lógica de negocio y actúa como puente hacia **Supabase** para autenticación, base de datos PostgreSQL y almacenamiento de archivos.

<p align="center">
  <img src="./assets/Arquitectura.png" alt="Arquitectura general del sistema Quickvnt" width="900" />
</p>

> **Principio clave:** La app Android **siempre habla con el backend propio**, nunca directamente con Supabase. El módulo `auth/` actúa como bridge hacia Supabase Auth, manteniendo la lógica de negocio centralizada.

---

## 2. Stack tecnológico

### 2.1 Backend (este repositorio)

| Componente | Tecnología | Justificación |
|---|---|---|
| Lenguaje | Python 3.10+ | Curva de entrada baja, ecosistema maduro |
| Framework | **FastAPI** | Async nativo, tipado con Pydantic, OpenAPI automático |
| Servidor ASGI | Uvicorn (dev) / Gunicorn + Uvicorn workers (prod) | Estándar para producción |
| ORM | SQLModel + SQLAlchemy 2.x (async) | Modelado tipado contra Postgres de Supabase |
| Validación | Pydantic v2 | DTOs de entrada/salida integrados en FastAPI |
| Auth bridge | `supabase-py` | Delegación de registro/login/refresh a Supabase Auth |
| JWT | `python-jose` / PyJWT | Validación de tokens emitidos por Supabase |
| QR | `qrcode` + Pillow | Generación y firma HMAC de códigos QR |
| Testing | pytest + httpx + pytest-asyncio | Pruebas unitarias e integración async |
| Contenedores | Docker + Docker Compose | Entorno reproducible |

### 2.2 Cliente móvil (repositorio separado)

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | Clean Architecture + MVVM |
| DI | Hilt (Dagger) |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines + Flow |
| Persistencia local | Room + DataStore |
| QR escaneo | CameraX + ML Kit Barcode Scanning |
| Imágenes | Coil |
| Gráficos | Vico / MPAndroidChart |

### 2.3 Infraestructura

| Servicio | Proveedor |
|---|---|
| Base de datos + Auth + Storage | Supabase (gestionado) |
| Hosting API (MVP) | Railway / Render / Fly.io |
| CI/CD | GitHub Actions |

---

## 3. Arquitectura del backend

### 3.1 Organización por módulos

```
app/
├── main.py                  # FastAPI app, routers, CORS, lifespan
├── core/
│   ├── config.py             # Pydantic Settings (env vars)
│   ├── security.py            # JWT validation, get_current_user, require_role
│   └── database.py            # Async SQLAlchemy engine/session
│
├── auth/
│   ├── router.py               # POST /auth/register, /login, /refresh
│   └── schemas.py
│
├── users/
│   ├── router.py                # GET/PUT /users/me
│   ├── models.py                 # Profile (SQLModel)
│   ├── schemas.py
│   └── profile_service.py
│
├── events/
│   ├── router.py                 # CRUD eventos + marketplace + staff
│   ├── models.py                  # Event, StaffAssignment
│   ├── schemas.py
│   └── staff_service.py
│
├── tickets/
│   ├── router.py                  # Registro a eventos + consulta tickets
│   ├── models.py                   # Ticket
│   ├── qr.py                        # Firma HMAC y verificación QR
│   └── schemas.py
│
├── checkin/
│   ├── router.py                   # POST /checkin/validate
│   ├── models.py                    # Checkin
│   └── schemas.py
│
└── analytics/
    └── router.py                   # GET /analytics/events/{id}
```

### 3.2 Patrón de capas

Cada módulo sigue:

```
Router (HTTP) → Service (lógica de negocio) → Model/ORM (acceso a datos)
```

Los `schemas.py` (Pydantic) separan los modelos de API de los modelos de base de datos (`models.py`).

### 3.3 Rol de Supabase Auth

1. **Registro/Login** ocurre contra Supabase Auth, pero la app Android habla con el backend propio.
2. El router `auth/` reenvía a Supabase Auth y devuelve los JWT al cliente.
3. Supabase Auth emite JWT con `sub` = user id. El backend **valida la firma** en `core/security.py`.
4. El **rol de negocio** (`ATTENDEE` / `ORGANIZER` / `STAFF`) se guarda en la tabla `profiles` (1:1 con `auth.users`).
5. Refresh de tokens queda delegado a Supabase Auth vía `POST /auth/refresh`.

### 3.4 Seguridad de los QR

1. Al registrarse, `tickets/qr.py` genera un payload firmado con **HMAC-SHA256** usando `QR_JWT_SECRET`.
2. El QR codifica `{ ticket_id, event_id, user_id }` + firma criptográfica.
3. En el escaneo, `/checkin/validate` **revalida contra la base de datos**: ticket existente, no cancelado, no ya consumido.
4. Esto evita QR falsificados (firma inválida) y QR reutilizados (estado `CHECKED_IN`).

### 3.5 Dashboard en tiempo real

Para el MVP se usa **polling corto** (cada 5–10 s) desde la app del organizador contra `GET /analytics/events/{event_id}`. Las consultas son agregadas (`COUNT`, tasas) directamente en PostgreSQL, sin Redis ni WebSockets.

---

## 4. Mapeo de funcionalidades del MVP

| # | Funcionalidad | Backend | Frontend Android |
|---|---|---|---|
| 1 | Gestión de asistentes (aforo + formulario dinámico) | `events` (schema JSONB) + `tickets` (validación transaccional) | `event_management` + `event_detail` |
| 2 | Control de acceso QR | `tickets` (emisión firmada) + `checkin` (validación idempotente) | `my_tickets` + `checkin` (CameraX/ML Kit) |
| 3 | Dashboard analítico | `analytics` (queries agregadas) | `organizer/dashboard` (polling + gráficos) |
| 4 | Marketplace de eventos | `GET /events` con filtros/paginación | `marketplace` |
| 5 | Login/registro con JWT | `auth` (bridge Supabase Auth) | `auth` + DataStore + AuthInterceptor |
| 6 | Gestión de staff | `events` (crear/asignar staff por evento) | `organizer` + `checkin` |

---

## 5. Estados del ticket

```
REGISTERED → CHECKED_IN → CANCELLED
```

Sin pagos en el MVP. El flujo es: registro → presentación de QR → validación en puerta.

---

## 6. Roadmap de implementación

1. **Base del proyecto**: FastAPI, Supabase (DB + Auth + Storage), Docker, CI con pytest.
2. **Auth + Users**: bridge Supabase Auth, tabla `profiles`, dependencias de seguridad.
3. **Events**: CRUD + marketplace público.
4. **Tickets**: registro + formulario dinámico + control de aforo.
5. **QR**: emisión firmada + pantalla "mi ticket".
6. **Check-in**: validación de QR en campo.
7. **Analytics**: KPIs una vez hay datos de tickets/check-ins.

---

## 7. Consideraciones post-MVP

- El monolito modular permite **extraer módulos** (ej. `analytics`) como microservicios si el tráfico lo exige.
- Pagos futuros: módulo `payments` sin afectar el modelo actual.
- Check-ins concurrentes masivos: evaluar Supabase Realtime o cola (Celery).
- Reforzar **Row-Level Security** en Supabase a medida que crezcan los organizadores.

---

## Información del equipo

| | |
|---|---|
| **Grupo** | Quickvnt |
| **Salón** | 1SF-241 |

| # | Integrante | Cédula |
|---|---|---|
| 1 | Fong, Enrique | 4-829-300 |
| 2 | González, Jabneel | 8-990-229 |
| 3 | Guillén, Manuel | 8-1016-1618 |
| 4 | Lu, Joaquín | 8-1024-2466 |
| 5 | Santimateo, Diego | 9-764-2382 |
| 6 | Pimentel, David | 8-1010-750 |
