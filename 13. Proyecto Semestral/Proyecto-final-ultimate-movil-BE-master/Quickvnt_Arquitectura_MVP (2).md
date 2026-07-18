# Arquitectura Quickvnt — MVP SaaS de Gestión de Eventos MICE

## 1. Visión general del sistema

Quickvnt se construye como un **monolito modular** (no microservicios) para el MVP: una sola aplicación backend desplegable, organizada internamente en módulos con responsabilidades separadas (auth, eventos, tickets, check-in, analítica). Supabase se usa como **base de datos gestionada y como sistema de autenticación/registro**, y el backend propio se implementa en **Python con FastAPI**.

<p align="center">
  <img src="./docs/assets/Arquitectura.png" alt="Arquitectura general del sistema Quickvnt" width="900" />
</p>

> **Nota:** el frontend (app Android) se mantiene sin cambios: **Kotlin + Retrofit** obligatorio. Lo que cambia en esta versión es únicamente el lenguaje/framework del backend: pasa de Kotlin/Spring Boot a **Python/FastAPI**. El contrato REST (endpoints, roles, modelo de datos) se mantiene idéntico, por lo que el frontend no requiere ningún ajuste.

---

## 2. Stack de tecnologías propuesto

| Capa | Tecnología | Justificación |
|---|---|---|
| **App móvil** | **Kotlin** (obligatorio) | Lenguaje nativo estándar de Android, requerido por el proyecto |
| Networking cliente | **Retrofit** (obligatorio) + OkHttp | Cliente HTTP tipado, interceptors para JWT, logging |
| UI | Jetpack Compose | UI declarativa moderna, menos boilerplate que XML |
| Arquitectura app | MVVM + Clean Architecture (capas data/domain/presentation) | Testeable, separación de responsabilidades |
| Inyección de dependencias | Hilt (Dagger) | Estándar de facto en Android/Kotlin |
| Async | Kotlin Coroutines + Flow | Manejo de I/O y estados reactivos |
| Serialización | kotlinx.serialization o Moshi | Integración directa con Retrofit |
| Almacenamiento local | Room (cache de eventos/tickets) + DataStore (tokens de sesión) | Soporte offline parcial y sesión segura |
| QR (mostrar) | ZXing Android Embedded | Renderizar el QR del ticket |
| QR (escanear) | CameraX + ML Kit Barcode Scanning | Escaneo fluido para el staff en campo |
| Gráficas (dashboard) | Vico (Compose Charts) o MPAndroidChart | Visualización de KPIs |
| Carga de imágenes | Coil | Carga de banners desde Supabase Storage |
| **Backend / API** | **Python 3.12+** + **FastAPI** | Framework async, tipado con Pydantic, genera documentación OpenAPI automáticamente, curva de entrada baja para iterar rápido en un MVP |
| Servidor ASGI | Uvicorn (dev) / Gunicorn + Uvicorn workers (prod) | Estándar para correr apps FastAPI en producción |
| Gestión de dependencias | Poetry (o `pip` + `requirements.txt`) | Entornos reproducibles |
| Validación de datos / esquemas | Pydantic v2 | Ya integrado en FastAPI, valida request/response DTOs |
| Acceso a datos | SQLAlchemy 2.x (modo async) + SQLModel, apuntando directamente al Postgres de Supabase | ORM maduro, soporta async nativo con FastAPI |
| Migraciones DB | Alembic (ejecutando contra la instancia de Supabase) | Versionado del esquema desde el día 1 |
| **Base de datos + Auth** | **Supabase** (PostgreSQL administrado + Supabase Auth) | Un solo servicio resuelve DB, registro, login, emisión de JWT y recuperación de contraseña |
| Cliente de Supabase en backend | `supabase-py` (o llamadas REST directas con `httpx`) | Para el módulo `auth` como bridge hacia Supabase Auth |
| Validación de JWT | `python-jose` o `PyJWT` | Verificar la firma de los tokens emitidos por Supabase Auth en cada request protegido |
| Generación de QR | `qrcode` + `Pillow` | Generar la imagen del QR firmado del lado del servidor |
| Almacenamiento de archivos | **Supabase Storage** | Banners de eventos y otros assets |
| Documentación API | Swagger UI / ReDoc (autogenerados por FastAPI) | No requiere configuración adicional, viene incluido |
| Testing backend | `pytest` + `httpx` (TestClient) + `pytest-asyncio` | Pruebas unitarias e de integración sobre endpoints async |
| Contenedores | Docker + Docker Compose | Entorno reproducible: servicio `api` (Python); Supabase es gestionado (o `supabase start` para desarrollo local) |
| CI/CD | GitHub Actions | Build, test y deploy automatizado |
| Hosting MVP | Railway / Render / Fly.io para la API (Supabase ya es hosting gestionado para DB/Auth/Storage) | Bajo costo y setup rápido |
| Observabilidad | Logging estructurado (`structlog`) + logs del proveedor de hosting | Suficiente para el MVP sin infraestructura adicional |

---

## 3. Arquitectura Backend (API)

### 3.1 Organización por módulos (monolito modular en Python)

```
app/
├── main.py                  # instancia de FastAPI, registro de routers, middlewares
├── core/
│   ├── config.py             # settings (Pydantic Settings): URL de Supabase, claves, etc.
│   ├── security.py            # validación de JWT de Supabase, dependencias de auth/roles
│   └── database.py            # engine/sesión async de SQLAlchemy contra Supabase Postgres
│
├── auth/
│   ├── router.py               # POST /auth/register, /login, /refresh (bridge hacia Supabase Auth)
│   └── schemas.py               # DTOs Pydantic de entrada/salida
│
├── users/
│   ├── router.py                # GET/PUT /users/me
│   ├── models.py                 # modelo `Profile` (SQLModel)
│   └── schemas.py
│
├── events/
│   ├── router.py                 # CRUD de eventos + marketplace
│   ├── models.py                  # modelo `Event`
│   └── schemas.py
│
├── tickets/
│   ├── router.py                  # registro a eventos, emisión de tickets + QR
│   ├── models.py                   # modelo `Ticket`
│   ├── qr.py                        # generación y firma del QR (HMAC + qrcode/Pillow)
│   └── schemas.py
│
├── checkin/
│   ├── router.py                   # POST /checkin/validate
│   └── models.py                    # modelo `Checkin`
│
├── analytics/
│   └── router.py                   # GET /events/{id}/analytics (queries agregadas)
│
└── common/
    ├── exceptions.py                # manejo centralizado de errores (HTTPException handlers)
    └── dependencies.py               # dependencias compartidas (paginación, roles, etc.)
```

Cada módulo sigue el patrón **Router (capa HTTP) → Service (lógica de negocio) → Repository/ORM (acceso a datos)**, con `schemas.py` (Pydantic) separando los modelos de API de los modelos de base de datos (`models.py`, SQLModel/SQLAlchemy).

### 3.2 Rol de Supabase Auth en el flujo de login/registro

1. **Registro/Login ocurre contra Supabase Auth**, pero la app Android **siempre habla con el backend propio** (no directo con Supabase), para mantener la lógica de negocio centralizada. El router `auth/` recibe `POST /api/v1/auth/register` y `/login`, reenvía la solicitud a la API de Supabase Auth (vía `supabase-py` o `httpx`), y devuelve al cliente el JWT que Supabase emite.
2. Supabase Auth **ya entrega JWT** (con `sub` = user id) — no es necesario implementar generación de tokens propia. El backend en FastAPI solo necesita **validar la firma** de esos JWT (usando el JWT secret o el JWKS público de Supabase, con `python-jose`) en una **dependencia de FastAPI** (`core/security.py`) que se inyecta en cada endpoint protegido.
3. El **rol de negocio** (`ATTENDEE` / `ORGANIZER`) se guarda en una tabla `profiles` propia (1 a 1 con el `auth.users` de Supabase). Tras validar el JWT, una dependencia adicional (`get_current_user`, `require_role("ORGANIZER")`) resuelve el rol y aplica las reglas de autorización por endpoint.
4. Refresh de tokens, recuperación de contraseña y verificación de email quedan delegados a Supabase Auth.

### 3.3 Endpoints principales (contrato REST) — sin cambios respecto a la versión anterior

```
Auth (bridge hacia Supabase Auth)
POST   /api/v1/auth/register          → crea usuario en Supabase Auth + fila en `profiles` con su rol
POST   /api/v1/auth/login             → delega en Supabase Auth, devuelve access + refresh token
POST   /api/v1/auth/refresh           → delega en Supabase Auth

Usuarios
GET    /api/v1/users/me
PUT    /api/v1/users/me

Eventos (marketplace + gestión)
GET    /api/v1/events                 → lista pública paginada/filtrable (marketplace)
GET    /api/v1/events/{id}
POST   /api/v1/events                 → [ORGANIZER] crea evento + formulario custom (JSON schema)
PUT    /api/v1/events/{id}             → [ORGANIZER]
DELETE /api/v1/events/{id}             → [ORGANIZER]
POST   /api/v1/events/{id}/staff       → [ORGANIZER] asigna staff al evento

Tickets / Registro
POST   /api/v1/events/{id}/register    → [ATTENDEE] envía formulario, genera ticket + QR (respetando el aforo)
GET    /api/v1/tickets/me              → tickets del usuario autenticado
GET    /api/v1/tickets/{id}

Check-in
POST   /api/v1/checkin/validate        → [STAFF/ORGANIZER] valida firma del QR y marca check-in (idempotente)

Analítica
GET    /api/v1/events/{id}/analytics   → [ORGANIZER] KPIs: tasa de asistencia, ritmo de registro, check-ins por hora
```

> El estado del ticket sigue simplificado (sin pagos): `REGISTERED → CHECKED_IN → CANCELLED`.

### 3.4 Seguridad de los QR (punto crítico del negocio)

1. Al confirmarse un registro (`POST /events/{id}/register`), el servicio de `tickets/qr.py` genera un **payload firmado**: `{ ticket_id, event_id, exp }` firmado con **HMAC-SHA256** usando una clave secreta propia del backend (independiente de las claves de Supabase), y produce la imagen del QR con la librería `qrcode` + `Pillow`.
2. En el escaneo, el endpoint `/checkin/validate` **siempre revalida contra la base de datos** (Supabase Postgres vía SQLAlchemy): verifica que el ticket exista, no esté ya `CHECKED_IN` y pertenezca al evento correcto — no confía únicamente en la firma del QR.
3. Esto evita QR falsificados (firma inválida) y QR reutilizados (estado ya consumido).

### 3.5 Actualización del dashboard en tiempo real

Sin Redis, la opción más simple para el MVP es **polling corto** (cada 5–10s) desde la app del organizador contra `GET /events/{id}/analytics`, resolviendo la consulta directamente contra Postgres (Supabase) con índices adecuados y queries agregadas en SQLAlchemy. Para el volumen esperado de un evento MICE pequeño/mediano esto es suficiente.

---

## 4. Modelo de datos (Supabase / PostgreSQL) — sin cambios

```
auth.users                 ← gestionado por Supabase Auth (no se modifica directamente)

profiles                   ← tabla propia, 1:1 con auth.users
 ├─ id (PK, FK → auth.users.id)
 ├─ name
 ├─ role                  [ATTENDEE | ORGANIZER]
 ├─ created_at

events
 ├─ id (PK, UUID)
 ├─ organizer_id (FK → profiles.id)
 ├─ title, description, category
 ├─ location, date_start, date_end
 ├─ capacity              → límite de aforo definido por el organizador
 ├─ banner_url             → referencia a un objeto en Supabase Storage
 ├─ status               [DRAFT | PUBLISHED | CLOSED | CANCELLED]
 ├─ custom_form_schema    (JSONB → campos dinámicos que el organizador pide al asistente)
 ├─ created_at

tickets
 ├─ id (PK, UUID)
 ├─ event_id (FK → events.id)
 ├─ user_id (FK → profiles.id)
 ├─ form_response         (JSONB → respuestas del asistente al formulario del organizador)
 ├─ qr_signature           (string, firma/token único)
 ├─ status                [REGISTERED | CHECKED_IN | CANCELLED]
 ├─ registered_at

checkins
 ├─ id (PK, UUID)
 ├─ ticket_id (FK → tickets.id, unique)
 ├─ validated_by (FK → profiles.id)   → staff/organizador que escaneó
 ├─ checkin_time

staff_assignments
 ├─ id (PK, UUID)
 ├─ event_id (FK → events.id)
 ├─ user_id (FK → profiles.id)
 ├─ assigned_at
```

**Notas de diseño (sin cambios):**
- `custom_form_schema` y `form_response` usan **JSONB** para soportar formularios dinámicos.
- El límite de aforo (`capacity`) se valida en el servicio de `tickets` con una transacción (`SELECT ... FOR UPDATE` en SQLAlchemy async, o constraint a nivel de aplicación) para evitar sobre-registro por condiciones de carrera.
- Definir **Row-Level Security (RLS)** en Supabase desde el inicio como capa extra de defensa.
- Índices recomendados: `events(status, date_start)`, `tickets(event_id, status)`, `tickets(user_id)`.

---

## 5. Arquitectura Frontend (Android – Kotlin) — sin cambios

### 5.1 Organización de capas (Clean Architecture)

```
app/
├── di/                     # módulos Hilt
├── core/
│   ├── network/            # Retrofit instance, interceptors (JWT), manejo de errores HTTP
│   ├── session/            # DataStore: guardar/leer tokens de sesión
│   └── ui/                 # theming, componentes Compose reutilizables
│
├── data/
│   ├── remote/              # interfaces Retrofit (AuthApi, EventsApi, TicketsApi, CheckinApi, AnalyticsApi)
│   ├── local/                # Room (cache de eventos/tickets para modo offline parcial)
│   ├── dto/                  # modelos de request/response
│   └── repository/           # implementaciones de los repositorios del dominio
│
├── domain/
│   ├── model/                # entidades de negocio puras (Event, Ticket, User)
│   ├── repository/            # interfaces (contratos) que data/ implementa
│   └── usecase/                # RegisterToEventUseCase, ValidateQrUseCase, GetAnalyticsUseCase, etc.
│
└── presentation/
    ├── auth/                  # LoginScreen, RegisterScreen + ViewModels
    ├── marketplace/            # lista/búsqueda de eventos (usuario común)
    ├── event_detail/            # detalle + formulario dinámico de registro
    ├── my_tickets/               # tickets del usuario + QR
    ├── organizer/
    │   ├── event_management/      # crear/editar evento, definir formulario y aforo
    │   └── dashboard/               # KPIs, gráficos
    └── checkin/                    # escáner QR (CameraX + ML Kit) para staff/organizador
```

### 5.2 Flujo de red (Retrofit) — sin cambios

- Una única instancia de `Retrofit` + `OkHttpClient` con:
  - **AuthInterceptor**: adjunta `Authorization: Bearer <access_token>` desde DataStore (token entregado por el backend FastAPI, que a su vez proviene de Supabase Auth).
  - **AuthAuthenticator** (de OkHttp): al recibir 401, intenta refrescar el token contra `/auth/refresh` y reintenta la petición.
  - **LoggingInterceptor** habilitado solo en builds de debug.
- La app **siempre habla con el backend propio (FastAPI)**, no directamente con Supabase.

### 5.3 Renderizado y escaneo de QR en el cliente — sin cambios

- **Mostrar QR (asistente):** el **backend FastAPI genera la imagen** del QR (con `qrcode`/`Pillow` del lado servidor); el cliente solo la muestra con Coil.
- **Escanear QR (staff):** CameraX captura el frame, ML Kit decodifica el string, y el ViewModel llama a `POST /checkin/validate`.

---

## 6. Mapeo de funcionalidades del MVP a la arquitectura

| # | Funcionalidad | Backend (FastAPI) | Frontend |
|---|---|---|---|
| 1 | Gestión de asistentes (aforo + formulario dinámico) | Módulo `events` (schema JSONB) + `tickets` (form_response, validación de capacidad transaccional) | Pantallas `event_management` (organizador) y `event_detail` con formulario dinámico (usuario común) |
| 2 | Control de acceso QR | Módulo `tickets` (emisión firmada con `qrcode`/`Pillow`) + `checkin` (validación idempotente) | `my_tickets` (mostrar QR) y `checkin` (escanear con CameraX/ML Kit) |
| 3 | Dashboard analítico | Módulo `analytics` con queries agregadas (SQLAlchemy) contra Supabase Postgres | `organizer/dashboard` con gráficos (Vico/MPAndroidChart) vía polling |
| 4 | Marketplace de eventos | Endpoint público `GET /events` con filtros/paginación | `marketplace` con búsqueda y listado (usuario común) |
| 5 | Login/registro con JWT | Módulo `auth` como bridge hacia **Supabase Auth**, validación de JWT con `python-jose` como dependencia de FastAPI | `auth` (Login/RegisterScreen) + `core/session` (DataStore) + `AuthInterceptor` (Retrofit) |

---

## 7. Roadmap sugerido de implementación (orden recomendado)

1. **Base del proyecto**: setup del proyecto FastAPI (Poetry, estructura de módulos), proyecto de Supabase (DB + Auth + Storage), Docker para la API, CI básico con `pytest`.
2. **Auth + Users**: conexión del backend con Supabase Auth, tabla `profiles` con roles, dependencias de seguridad de FastAPI (`get_current_user`, `require_role`) → desbloquea todo lo demás.
3. **Events (CRUD + marketplace)**: creación de eventos por organizador y listado público.
4. **Tickets (registro + formulario dinámico + control de aforo)**.
5. **QR (emisión firmada con `qrcode`/`Pillow` + pantalla de "mi ticket")**.
6. **Checkin (validación de QR en campo)**.
7. **Analytics/Dashboard** (una vez hay datos reales de tickets/checkins que agregar).

---

## 8. Consideraciones para escalar después del MVP

- El monolito modular en FastAPI está diseñado para poder **extraer módulos** (ej. `analytics`) como servicios independientes si el tráfico lo exige — FastAPI facilita esto al ser cada router relativamente autocontenido.
- Si más adelante se retoma el cobro de boletos dentro de la app (fuera de alcance de este MVP), se puede añadir un módulo `payments` y una pasarela (Yappy/Stripe/PayPal) sin afectar el resto del modelo.
- Si el volumen de check-ins concurrentes crece mucho, se puede evaluar Supabase Realtime o una cola simple (ej. Celery + un broker liviano) para desacoplar el pico de escaneos del resto del sistema.
- Reforzar Row-Level Security en Supabase a medida que crezca la cantidad de organizadores/clientes empresariales.

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