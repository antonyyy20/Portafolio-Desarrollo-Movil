# Arquitectura de la Aplicación Android Quickvnt

## 1. Introducción

La aplicación móvil cliente de **Quickvnt** es la interfaz principal a través de la cual los tres actores del sistema (asistentes, organizadores y personal de staff) interactúan con la plataforma SaaS de gestión de eventos MICE.

El desarrollo se estructuró bajo **Clean Architecture** y el patrón **MVVM** (Model-View-ViewModel), aislando la UI de los detalles de red, bases de datos y persistencia local. Esto facilita el mantenimiento, incrementa la testabilidad y asegura que flujos críticos (como el escaneo de tickets) operen de manera robusta.

---

## 2. Stack tecnológico del frontend

### 2.1 Kotlin

Lenguaje oficial de Android. Proporciona null safety, inferencia de tipos y corrutinas nativas para operaciones asíncronas.

### 2.2 Jetpack Compose

Kit de UI declarativo moderno. Elimina layouts XML y permite describir la interfaz mediante funciones de Kotlin que reaccionan a cambios de estado. Ideal para formularios dinámicos y componentes reutilizables.

### 2.3 Hilt (Dagger)

Inyección de dependencias estándar en Android. Automatiza gráficos de dependencias y gestiona el ciclo de vida de objetos en ViewModels, Activities y Fragments.

### 2.4 Retrofit y OkHttp

- **Retrofit**: interfaces de red tipadas con serialización JSON automática.
- **OkHttp**: motor HTTP con interceptores (`AuthInterceptor`) y autenticadores (`AuthAuthenticator`) para JWT y refresh silencioso de sesión.

### 2.5 Room Database y Jetpack DataStore

| Tecnología | Uso |
|---|---|
| **Room** | Caché local relacional (SQLite) de eventos y tickets para acceso offline parcial |
| **DataStore** | Almacenamiento clave-valor asíncrono para tokens de sesión y preferencias |

### 2.6 CameraX y Google ML Kit

- **CameraX**: control de cámara simplificado para múltiples dispositivos Android.
- **ML Kit Barcode Scanning**: decodificación local de códigos QR en milisegundos.

### 2.7 Coil

Carga asíncrona de banners de eventos desde Supabase Storage. Integración nativa con Compose (`AsyncImage`), caché en memoria y disco, y cancelación automática según ciclo de vida.

---

## 3. Arquitectura de software

### 3.1 Unidirectional Data Flow (UDF) y MVVM

```
Usuario → Vista (Compose) → ViewModel → UseCase → Repository → API/Room
                ↑                                                      │
                └──────────── StateFlow (estado inmutable) ←──────────┘
```

- La UI observa el estado del ViewModel como `StateFlow`.
- Las interacciones del usuario emiten eventos al ViewModel.
- El ViewModel ejecuta casos de uso y emite nuevo estado inmutable.
- Compose redibuja solo los componentes modificados (recomposición).

### 3.2 Capas (Clean Architecture)

```
┌─────────────────────────────────────────┐
│           PRESENTACIÓN (ui/)            │
│  Compose Screens + ViewModels           │
└─────────────────┬───────────────────────┘
                  │ depende de
┌─────────────────▼───────────────────────┐
│            DOMINIO (domain/)            │
│  Entidades + UseCases + Interfaces Repo │
└─────────────────┬───────────────────────┘
                  │ implementado por
┌─────────────────▼───────────────────────┐
│             DATOS (data/)               │
│  Retrofit DTOs + Room + DataStore       │
│  + Implementaciones de repositorios       │
└─────────────────────────────────────────┘
```

| Capa | Responsabilidad |
|---|---|
| **Presentación** | UI Compose, ViewModels, navegación |
| **Dominio** | Entidades puras (`Event`, `Ticket`, `Profile`), casos de uso, contratos de repositorio |
| **Datos** | Implementación de repositorios, mapeo DTO → dominio, red y persistencia local |

---

## 4. Estructura del proyecto

```
frontend/app/src/main/java/com/example/frontend/
│
├── di/                     # Módulos Hilt
│
├── core/                   # Componentes transversales
│   ├── network/            # Retrofit, interceptores, AuthAuthenticator
│   ├── session/            # SessionManager + DataStore
│   └── ui/                 # Temas, tipografías, componentes comunes
│
├── domain/
│   ├── model/              # Event, Ticket, UserProfile
│   ├── repository/         # Interfaces (contratos)
│   └── usecase/            # GetAnalyticsUseCase, ValidateQrUseCase, etc.
│
├── data/
│   ├── remote/             # AuthApi, EventsApi, TicketsApi, CheckinApi, AnalyticsApi
│   ├── local/              # Room (entidades, DAOs) + DataStore
│   └── repository/         # Implementaciones concretas
│
├── navigation/             # Grafo de Compose Navigation
│
└── ui/                     # Pantallas por módulo de negocio
    ├── auth/               # LoginScreen, RegisterScreen
    ├── marketplace/        # Listado y filtros de eventos
    ├── event_detail/       # Detalle + formulario dinámico
    ├── my_tickets/         # Tickets del usuario + QR
    ├── checkin/            # Escáner QR (CameraX + ML Kit)
    └── organizer/          # Panel, aforo, analíticas, gestión de staff
```

---

## 5. Ciclo de red, sesión y seguridad

### 5.1 Flujo de autenticación

1. Login exitoso → backend retorna `access_token` + `refresh_token`.
2. Tokens se almacenan en **DataStore** (con Jetpack Security en producción).
3. `AuthInterceptor` añade `Authorization: Bearer <access_token>` a cada petición.

### 5.2 Refresh silencioso de tokens

```
Petición protegida → 401 Unauthorized
    → AuthAuthenticator captura el error
    → POST /api/v1/auth/refresh con refresh_token
    → Actualiza tokens en DataStore
    → Reintenta petición original
    → Si falla refresh → logout forzado → LoginScreen
```

### 5.3 Seguridad en tránsito

- Toda comunicación bajo **HTTPS/TLS 1.2+**.
- Credenciales solo en el **body** de peticiones POST (nunca en query params).
- Certificate Pinning opcional en producción.

---

## 6. Módulo de escaneo de acceso

### 6.1 Integración cámara en Compose

`AndroidView` incrusta `PreviewView` de CameraX dentro del layout declarativo de Compose.

### 6.2 Procesamiento de frames

| Caso de uso CameraX | Función |
|---|---|
| **Preview** | Imagen en vivo para apuntar al QR |
| **ImageAnalysis** | Frames enviados a ML Kit para decodificación |

### 6.3 Flujo post-escaneo

1. ML Kit decodifica el string del QR.
2. Throttling: cámara desactivada 2–3 s tras lectura exitosa.
3. ViewModel invoca `ValidateQrUseCase` → `POST /api/v1/checkin/validate`.
4. Respuesta exitosa → pantalla verde + sonido de éxito.
5. Error (inválido/duplicado) → pantalla roja + alerta sonora.

---

## 7. Concurrencia y rendimiento

| Operación | Dispatcher |
|---|---|
| Red (Retrofit) | `Dispatchers.IO` |
| Room / DataStore | `Dispatchers.IO` |
| Decodificación QR (ML Kit) | Pool de hilos dedicado |
| Actualización de UI | `Dispatchers.Main` |

El estado se expone como `StateFlow` y se consume con `.collectAsStateWithLifecycle()` para respetar el ciclo de vida del componente.

---

## 8. Navegación por rol

| Rol | Pantallas principales |
|---|---|
| **ATTENDEE** | Marketplace → Detalle evento → Registro → Mis Tickets (QR) |
| **ORGANIZER** | Mis Eventos → Crear/Editar → Dashboard → Gestión Staff |
| **STAFF** | Mis Colaboraciones → Escáner QR del evento asignado |

---

## 9. Conclusiones

La arquitectura Android de Quickvnt combina **Clean Architecture + MVVM** con tecnologías modernas del ecosistema Google: Compose, Hilt, CameraX, ML Kit y Retrofit con refresh automático de sesión. Esta base proporciona un MVP robusto y preparado para escalar a producción comercial con refactorización mínima.

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
