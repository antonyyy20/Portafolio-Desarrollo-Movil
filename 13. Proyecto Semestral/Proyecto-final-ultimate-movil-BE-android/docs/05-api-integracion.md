# 05 — API e integración

> **Grupo:** Quickvnt · **Salón:** 1SF-241

Documentación de los endpoints consumidos por la app Android.

**Base URL (producción):** `https://pruebasemestral.onrender.com/api/v1/`

**Autenticación:** Bearer JWT en header `Authorization`

![Arquitectura Quickvnt](assets/arquitectura.png)

La app Android (capa **Data** → Retrofit) consume esta API según el diagrama anterior.

---

## Configuración en Android

```kotlin
// app/build.gradle.kts
buildConfigField("String", "API_BASE_URL", "\"https://pruebasemestral.onrender.com/api/v1/\"")
```

Cliente definido en `QuickvntApi.kt` usando Retrofit.

---

## Endpoints

### Autenticación

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `POST` | `/auth/register` | Registro de usuario | RegisterScreen |
| `POST` | `/auth/login` | Inicio de sesión | LoginScreen |
| `POST` | `/auth/refresh` | Renovar token | Automático |

**Request login:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña"
}
```

**Response:**
```json
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "token_type": "bearer"
}
```

---

### Usuario

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `GET` | `/users/me` | Obtener perfil | ProfileScreen |
| `PUT` | `/users/me` | Actualizar perfil | ProfileScreen |

---

### Eventos

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `GET` | `/events` | Listar eventos publicados | MarketplaceScreen |
| `GET` | `/events?category={cat}` | Filtrar por categoría | MarketplaceScreen |
| `GET` | `/events/{id}` | Detalle de evento | EventDetailScreen |
| `GET` | `/events/mine` | Mis eventos (organizador) | MyEventsScreen |
| `POST` | `/events` | Crear evento | CreateEventScreen |
| `PUT` | `/events/{id}` | Actualizar evento | EditEventScreen |
| `DELETE` | `/events/{id}` | Eliminar evento | MyEventsScreen |

**Query params para listar:**
- `category` — Filtro por categoría
- `status_filter` — Default: `PUBLISHED`
- `skip` / `limit` — Paginación

---

### Staff

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `GET` | `/events/staff/mine` | Eventos asignados al staff | StaffEventsScreen |
| `GET` | `/events/{id}/staff` | Listar staff del evento | ManageStaffScreen |
| `POST` | `/events/{id}/staff` | Agregar staff | ManageStaffScreen |
| `DELETE` | `/events/{id}/staff/{userId}` | Remover staff | ManageStaffScreen |

---

### Tickets

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `POST` | `/tickets/register/{eventId}` | Registrarse a evento | RegisterEventScreen |
| `GET` | `/tickets/me` | Mis tickets | MyTicketsScreen |
| `GET` | `/tickets/{id}` | Detalle de ticket | TicketDetailScreen |

---

### Check-in

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `POST` | `/checkin/validate` | Validar QR de asistente | QrScannerScreen |

**Request:**
```json
{
  "qr_payload": "ticket-uuid-o-codigo",
  "event_id": "uuid-del-evento"
}
```

**Response:**
```json
{
  "valid": true,
  "message": "Check-in exitoso",
  "attendee_name": "Juan Pérez"
}
```

---

### Analytics

| Método | Endpoint | Descripción | Pantalla |
|--------|----------|-------------|----------|
| `GET` | `/analytics/events/{eventId}` | KPIs del evento | AnalyticsScreen |

---

## Flujo de red en Android

```text
ViewModel
    ↓
Repository (AuthRepository / EventRepository / TicketRepository)
    ↓
QuickvntApi (Retrofit)
    ↓
AuthInterceptor (agrega Bearer token)
    ↓
OkHttp → HTTPS → FastAPI
```

---

## Manejo de errores

Implementado en `NetworkErrors.kt`:

| Código HTTP | Manejo en app |
|-------------|---------------|
| 401 | Token expirado → intentar refresh o logout |
| 403 | Sin permisos para la acción |
| 404 | Recurso no encontrado |
| 422 | Error de validación (mostrar mensaje) |
| 500 | Error del servidor (mensaje genérico) |
| Timeout | Mensaje de conexión lenta |

---

## DTOs principales

| DTO | Archivo | Uso |
|-----|---------|-----|
| `LoginRequest` / `RegisterRequest` | AuthDtos.kt | Auth |
| `TokenResponse` | AuthDtos.kt | Tokens JWT |
| `ProfileResponse` | AuthDtos.kt | Perfil |
| `EventResponse` | EventDtos.kt | Eventos |
| `EventCreateRequest` | EventDtos.kt | Crear/editar |
| `TicketResponse` | TicketDtos.kt | Tickets |
| `CheckinRequest/Response` | TicketDtos.kt | Check-in |
| `AnalyticsResponse` | EventDtos.kt | Analytics |

---

## Prueba de conectividad

El test instrumentado `ApiConnectivityTest.kt` verifica que la API responde:

```bash
./gradlew :app:connectedDebugAndroidTest
```

---

## Documentación interactiva del backend

<!-- TODO: Verificar si Swagger está habilitado en producción -->

Si el backend expone Swagger UI:

```
https://pruebasemestral.onrender.com/docs
```

![Swagger UI](assets/screenshots/16-swagger.png)

---

## Diagrama de integración

<!-- TODO: Subir diagrama -->
![Integración API](assets/diagrams/integracion-api.png)

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
