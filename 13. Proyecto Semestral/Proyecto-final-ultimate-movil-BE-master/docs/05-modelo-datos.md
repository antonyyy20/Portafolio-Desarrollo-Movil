# Modelo de Datos

Base de datos: **PostgreSQL** (Supabase). Esquema oficial en [`schema.sql`](../schema.sql).

---

## Diagrama de relaciones

```
auth.users (Supabase Auth)
    │
    │ 1:1
    ▼
profiles ──────────────────────────────────────────┐
    │                                               │
    │ organizer_id                                  │ user_id
    ▼                                               │
events ◄──────────────── staff_assignments ─────────┤
    │                                               │
    │ event_id                                      │ user_id
    ▼                                               │
tickets ────────────────────────────────────────────┘
    │
    │ ticket_id (unique)
    ▼
checkins
```

---

## Tablas

### `auth.users` (gestionada por Supabase)

No se modifica directamente desde la aplicación. Contiene credenciales y metadatos de autenticación.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | UUID | Identificador único (PK) |
| `email` | text | Correo electrónico |
| `encrypted_password` | text | Contraseña hasheada |

---

### `profiles`

Perfil de negocio vinculado 1:1 con `auth.users`.

| Campo | Tipo | Constraints | Descripción |
|---|---|---|---|
| `id` | UUID | PK, FK → `auth.users.id` ON DELETE CASCADE | Mismo ID que Supabase Auth |
| `name` | text | NOT NULL | Nombre del usuario |
| `role` | text | NOT NULL, CHECK | `ATTENDEE`, `ORGANIZER` o `STAFF` |
| `created_at` | timestamptz | NOT NULL, DEFAULT now() | Fecha de creación |

**Políticas RLS:**
- `select_profiles`: lectura para usuarios autenticados.
- `update_own_profile`: actualización solo del propio perfil.

---

### `events`

Eventos creados por organizadores.

| Campo | Tipo | Constraints | Descripción |
|---|---|---|---|
| `id` | UUID | PK, DEFAULT gen_random_uuid() | Identificador del evento |
| `organizer_id` | UUID | FK → `profiles.id` ON DELETE CASCADE | Organizador propietario |
| `title` | text | NOT NULL | Título del evento |
| `description` | text | — | Descripción detallada |
| `category` | text | — | Categoría (Conferencia, Taller, etc.) |
| `location` | text | NOT NULL | Ubicación física |
| `date_start` | timestamptz | NOT NULL | Inicio del evento |
| `date_end` | timestamptz | NOT NULL, CHECK ≥ date_start | Fin del evento |
| `capacity` | int | NOT NULL, CHECK > 0 | Aforo máximo |
| `banner_url` | text | — | URL del banner en Supabase Storage |
| `status` | text | NOT NULL, DEFAULT 'DRAFT' | `DRAFT`, `PUBLISHED`, `CLOSED`, `CANCELLED` |
| `custom_form_schema` | jsonb | NOT NULL, DEFAULT '{}' | Esquema del formulario dinámico |
| `created_at` | timestamptz | NOT NULL, DEFAULT now() | Fecha de creación |

**Índices:**
- `events_organizer_id_idx` — FK del organizador.
- `events_published_status_date_start_idx` — índice parcial para marketplace (`WHERE status = 'PUBLISHED'`).

**Políticas RLS:**
- `select_published_events`: lectura pública de eventos publicados.
- `select_own_events`: organizador ve sus propios eventos.
- `manage_own_events`: CRUD solo para organizadores propietarios.

---

### `tickets`

Registros de asistentes a eventos.

| Campo | Tipo | Constraints | Descripción |
|---|---|---|---|
| `id` | UUID | PK, DEFAULT gen_random_uuid() | Identificador del ticket |
| `event_id` | UUID | FK → `events.id` ON DELETE CASCADE | Evento asociado |
| `user_id` | UUID | FK → `profiles.id` ON DELETE CASCADE | Asistente registrado |
| `form_response` | jsonb | NOT NULL, DEFAULT '{}' | Respuestas al formulario dinámico |
| `qr_signature` | text | NOT NULL | Firma HMAC del código QR |
| `status` | text | NOT NULL, DEFAULT 'REGISTERED' | `REGISTERED`, `CHECKED_IN`, `CANCELLED` |
| `registered_at` | timestamptz | NOT NULL, DEFAULT now() | Fecha de registro |

**Constraints:**
- `unique_user_event_ticket`: un usuario solo puede tener un ticket activo por evento.

**Índices:**
- `tickets_event_id_status_idx` — consultas de aforo y analítica.
- `tickets_user_id_idx` — listado de tickets del usuario.

**Políticas RLS:**
- `view_own_tickets`: el asistente ve sus propios tickets.
- `view_organizer_tickets`: el organizador ve tickets de sus eventos.
- `book_ticket`: inserción solo para el propio usuario.
- `cancel_own_ticket`: cancelación solo del propio ticket.

---

### `staff_assignments`

Asignación de personal de staff a eventos específicos.

| Campo | Tipo | Constraints | Descripción |
|---|---|---|---|
| `id` | UUID | PK, DEFAULT gen_random_uuid() | Identificador de la asignación |
| `event_id` | UUID | FK → `events.id` ON DELETE CASCADE | Evento asignado |
| `user_id` | UUID | FK → `profiles.id` ON DELETE CASCADE | Usuario staff |
| `assigned_at` | timestamptz | NOT NULL, DEFAULT now() | Fecha de asignación |

**Constraints:**
- `unique_event_staff`: un usuario solo puede estar asignado una vez por evento.

**Índices:**
- `staff_assignments_event_id_idx`
- `staff_assignments_user_id_idx`

---

### `checkins`

Registro de accesos validados en puerta.

| Campo | Tipo | Constraints | Descripción |
|---|---|---|---|
| `id` | UUID | PK, DEFAULT gen_random_uuid() | Identificador del check-in |
| `ticket_id` | UUID | FK → `tickets.id` ON DELETE CASCADE, UNIQUE | Ticket consumido (un solo check-in) |
| `validated_by` | UUID | FK → `profiles.id` ON DELETE SET NULL | Staff u organizador que validó |
| `checkin_time` | timestamptz | NOT NULL, DEFAULT now() | Momento del ingreso |

**Índices:**
- `checkins_validated_by_idx`

---

## Estados y transiciones

### Eventos

```
DRAFT → PUBLISHED → CLOSED
                  → CANCELLED
```

### Tickets

```
REGISTERED → CHECKED_IN
REGISTERED → CANCELLED
```

---

## Formulario dinámico (`custom_form_schema`)

El organizador define campos en JSONB al crear el evento. Ejemplo:

```json
{
  "fields": [
    {
      "name": "alergias",
      "type": "text",
      "label": "¿Alergias alimentarias?",
      "required": false
    },
    {
      "name": "talla",
      "type": "select",
      "label": "Talla de camiseta",
      "options": ["S", "M", "L", "XL"],
      "required": true
    }
  ]
}
```

Las respuestas del asistente se almacenan en `tickets.form_response`:

```json
{
  "alergias": "Ninguna",
  "talla": "M"
}
```

---

## Control de aforo

El backend valida capacidad con bloqueo pesimista:

```sql
SELECT * FROM events WHERE id = :event_id FOR UPDATE;
SELECT COUNT(*) FROM tickets WHERE event_id = :event_id AND status != 'CANCELLED';
```

Si `count >= capacity` → rechazo con HTTP 400.

---

## Migraciones

| Archivo | Descripción |
|---|---|
| `schema.sql` | Esquema completo inicial (tablas, índices, RLS) |
| `migrations/001_add_staff_role.sql` | Agrega rol `STAFF` al CHECK de `profiles.role` |

Para bases de datos existentes sin el rol STAFF, ejecutar la migración en el SQL Editor de Supabase.

---

## Índices recomendados (resumen)

| Índice | Tabla | Columnas | Propósito |
|---|---|---|---|
| `events_organizer_id_idx` | events | organizer_id | FK lookup |
| `events_published_status_date_start_idx` | events | status, date_start (parcial) | Marketplace |
| `tickets_event_id_status_idx` | tickets | event_id, status | Aforo y analítica |
| `tickets_user_id_idx` | tickets | user_id | Mis tickets |
| `staff_assignments_event_id_idx` | staff_assignments | event_id | Staff por evento |
| `checkins_validated_by_idx` | checkins | validated_by | Auditoría |

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
