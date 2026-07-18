# Referencia de API REST

Base URL: `/api/v1`

Autenticación: `Authorization: Bearer <access_token>` (excepto endpoints públicos marcados).

Documentación interactiva: `http://127.0.0.1:8000/docs`

---

## Autenticación (`/auth`)

### POST `/auth/register`

Crea usuario en Supabase Auth y perfil en `profiles`.

**Auth requerida:** No

**Body:**

```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña-segura",
  "name": "Juan Pérez",
  "role": "ATTENDEE"
}
```

| Campo | Tipo | Valores | Default |
|---|---|---|---|
| `email` | string (email) | — | requerido |
| `password` | string | — | requerido |
| `name` | string | — | requerido |
| `role` | string | `ATTENDEE`, `ORGANIZER` | `ATTENDEE` |

**Respuesta 200:** `TokenResponse`

```json
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "token_type": "bearer",
  "user_id": "uuid",
  "role": "ATTENDEE",
  "name": "Juan Pérez"
}
```

---

### POST `/auth/login`

Autentica con Supabase Auth y devuelve tokens + perfil.

**Auth requerida:** No

**Body:**

```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña-segura"
}
```

**Respuesta 200:** `TokenResponse`

---

### POST `/auth/refresh`

Renueva tokens usando el refresh token.

**Auth requerida:** No

**Body:**

```json
{
  "refresh_token": "eyJ..."
}
```

**Respuesta 200:** `TokenResponse`

---

## Usuarios (`/users`)

### GET `/users/me`

Obtiene el perfil del usuario autenticado.

**Auth requerida:** Sí

**Respuesta 200:** `ProfileResponse`

```json
{
  "id": "uuid",
  "name": "Juan Pérez",
  "role": "ATTENDEE",
  "created_at": "2026-01-15T10:00:00Z"
}
```

---

### PUT `/users/me`

Actualiza el perfil del usuario autenticado.

**Auth requerida:** Sí

**Body:**

```json
{
  "name": "Nuevo Nombre"
}
```

**Respuesta 200:** `ProfileResponse`

---

## Eventos (`/events`)

### GET `/events`

Lista eventos (marketplace). Endpoint **público**.

**Auth requerida:** No

**Query params:**

| Parámetro | Tipo | Default | Descripción |
|---|---|---|---|
| `category` | string | — | Filtrar por categoría |
| `status_filter` | string | `PUBLISHED` | Estado del evento |
| `skip` | int | `0` | Offset de paginación |
| `limit` | int | `20` | Máximo de resultados (1–100) |

**Respuesta 200:** `EventResponse[]`

---

### GET `/events/{id}`

Detalle de un evento específico. Endpoint **público**.

**Auth requerida:** No

**Respuesta 200:** `EventResponse`

---

### GET `/events/mine`

Lista eventos creados por el organizador autenticado.

**Auth requerida:** Sí — rol `ORGANIZER`

**Query params:** `skip`, `limit`

**Respuesta 200:** `EventResponse[]`

---

### GET `/events/staff/mine`

Lista eventos donde el usuario autenticado está asignado como staff.

**Auth requerida:** Sí

**Respuesta 200:** `EventResponse[]`

---

### POST `/events`

Crea un nuevo evento.

**Auth requerida:** Sí — rol `ORGANIZER`

**Body:**

```json
{
  "title": "Conferencia Tech 2026",
  "description": "Evento anual de tecnología",
  "category": "Conferencia",
  "location": "Centro de Convenciones, Ciudad",
  "date_start": "2026-06-15T09:00:00Z",
  "date_end": "2026-06-15T18:00:00Z",
  "capacity": 500,
  "banner_url": "https://supabase.co/storage/.../banner.jpg",
  "custom_form_schema": {
    "fields": [
      { "name": "alergias", "type": "text", "label": "¿Alergias alimentarias?", "required": false },
      { "name": "talla", "type": "select", "label": "Talla de camiseta", "options": ["S", "M", "L", "XL"], "required": true }
    ]
  }
}
```

**Respuesta 201:** `EventResponse` (status inicial: `DRAFT`)

---

### PUT `/events/{id}`

Actualiza un evento existente. Solo el organizador propietario.

**Auth requerida:** Sí — rol `ORGANIZER`

**Body:** `EventUpdate` (todos los campos opcionales)

```json
{
  "status": "PUBLISHED",
  "capacity": 600
}
```

**Valores de `status`:** `DRAFT`, `PUBLISHED`, `CLOSED`, `CANCELLED`

**Respuesta 200:** `EventResponse`

---

### DELETE `/events/{id}`

Elimina un evento. Solo el organizador propietario.

**Auth requerida:** Sí — rol `ORGANIZER`

**Respuesta 204:** Sin contenido

---

## Staff de eventos (`/events/{id}/staff`)

### GET `/events/{id}/staff`

Lista miembros del staff asignados al evento.

**Auth requerida:** Sí — rol `ORGANIZER` (propietario del evento)

**Respuesta 200:** `StaffMemberResponse[]`

---

### POST `/events/{id}/staff`

Crea una cuenta de staff en Supabase Auth y la asigna al evento.

**Auth requerida:** Sí — rol `ORGANIZER` (propietario del evento)

**Body:**

```json
{
  "email": "staff@ejemplo.com",
  "password": "password123",
  "name": "María Staff"
}
```

**Respuesta 201:** `StaffMemberResponse`

---

### DELETE `/events/{id}/staff/{user_id}`

Remueve la asignación de staff del evento.

**Auth requerida:** Sí — rol `ORGANIZER` (propietario del evento)

**Respuesta 204:** Sin contenido

---

## Tickets (`/tickets`)

### POST `/tickets/register/{event_id}`

Registra al usuario autenticado en un evento publicado.

**Auth requerida:** Sí

**Body:**

```json
{
  "event_id": "uuid-del-evento",
  "form_response": {
    "alergias": "Ninguna",
    "talla": "M"
  }
}
```

**Respuesta 201:** `TicketResponse`

**Errores comunes:**

| Código | Detalle |
|---|---|
| 400 | Ya registrado / evento no publicado / aforo completo |
| 404 | Evento no encontrado |

---

### GET `/tickets/me`

Lista todos los tickets del usuario autenticado.

**Auth requerida:** Sí

**Respuesta 200:** `TicketResponse[]`

---

### GET `/tickets/{id}`

Detalle de un ticket. Accesible por el dueño del ticket o el organizador del evento.

**Auth requerida:** Sí

**Respuesta 200:** `TicketResponse`

---

## Check-in (`/checkin`)

### POST `/checkin/validate`

Valida un ticket mediante QR y registra el ingreso.

**Auth requerida:** Sí — organizador del evento o staff asignado con rol `STAFF`

**Body:**

```json
{
  "ticket_id": "uuid-del-ticket",
  "event_id": "uuid-del-evento",
  "qr_signature": "firma-hmac-del-qr"
}
```

**Respuesta 201:** `CheckinResponse`

```json
{
  "id": "uuid",
  "ticket_id": "uuid",
  "validated_by": "uuid-del-validador",
  "checkin_time": "2026-06-15T09:30:00Z"
}
```

**Errores comunes:**

| Código | Detalle |
|---|---|
| 400 | Firma inválida / ticket cancelado / ticket de otro evento |
| 403 | Sin permisos de validación |
| 404 | Ticket o evento no encontrado |

> **Idempotencia:** Si el ticket ya fue consumido, retorna el check-in existente sin error.

---

## Analítica (`/analytics`)

### GET `/analytics/events/{event_id}`

KPIs agregados del evento.

**Auth requerida:** Sí — organizador propietario del evento

**Respuesta 200:**

```json
{
  "event_id": "uuid",
  "capacity": 500,
  "total_registered": 320,
  "total_checked_in": 280,
  "occupancy_rate_percent": 64.0,
  "attendance_rate_percent": 87.5,
  "status": "PUBLISHED"
}
```

---

## Endpoints de sistema

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/` | No | Mensaje de bienvenida |
| `GET` | `/db-test-events` | No | Prueba de conexión a la base de datos |

---

## Resumen de permisos por rol

| Endpoint | ATTENDEE | ORGANIZER | STAFF |
|---|---|---|---|
| Marketplace (`GET /events`) | ✅ | ✅ | ✅ |
| Registro a evento | ✅ | — | — |
| Mis tickets | ✅ | — | — |
| CRUD eventos | — | ✅ (propios) | — |
| Analytics | — | ✅ (propios) | — |
| Gestionar staff | — | ✅ (propios) | — |
| Validar check-in | — | ✅ (propios) | ✅ (asignados) |
| Eventos staff (`/staff/mine`) | — | — | ✅ |

---

## Códigos HTTP utilizados

| Código | Uso |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado (ticket, check-in, evento, staff) |
| 204 | Eliminación exitosa |
| 400 | Validación de negocio fallida |
| 401 | Token inválido o expirado |
| 403 | Sin permisos para la operación |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |

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
