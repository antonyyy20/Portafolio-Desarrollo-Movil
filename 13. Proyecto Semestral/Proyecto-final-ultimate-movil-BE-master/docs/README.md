# Documentación Quickvnt — MVP SaaS de Gestión de Eventos MICE

Bienvenido a la documentación oficial del proyecto **Quickvnt**. Este repositorio contiene el backend (API REST en Python/FastAPI) que complementa la aplicación móvil Android (Kotlin).

## Índice de documentación

| Documento | Descripción |
|---|---|
| [Arquitectura del sistema](./01-arquitectura-sistema.md) | Visión general, stack tecnológico, módulos del backend, modelo de datos y roadmap |
| [Arquitectura Android](./02-arquitectura-android.md) | Clean Architecture, MVVM, stack del cliente móvil y flujos de red |
| [Funcionalidades end-to-end](./03-funcionalidades-end-to-end.md) | Flujos completos desde la app móvil hasta la base de datos, con diagramas |
| [Referencia de API](./04-api-reference.md) | Contrato REST completo: endpoints, roles, códigos de respuesta |
| [Modelo de datos](./05-modelo-datos.md) | Esquema PostgreSQL/Supabase, relaciones, índices y políticas RLS |
| [Instalación y despliegue](./06-instalacion-despliegue.md) | Configuración local, variables de entorno y puesta en producción |

## Visión rápida del sistema

<p align="center">
  <img src="./assets/Arquitectura.png" alt="Arquitectura general del sistema Quickvnt" width="900" />
</p>

> La app Android **siempre habla con el backend propio**, nunca directamente con Supabase.

## Actores del sistema

| Rol | Descripción |
|---|---|
| **ATTENDEE** | Descubre eventos en el marketplace, se registra con formulario dinámico y presenta su QR de acceso |
| **ORGANIZER** | Crea y publica eventos, define aforo y formularios, asigna staff, consulta analíticas |
| **STAFF** | Valida entradas escaneando códigos QR en el evento (asignado por el organizador) |

## Enlaces útiles (desarrollo local)

| Recurso | URL |
|---|---|
| API raíz | `http://127.0.0.1:8000/` |
| Swagger UI | `http://127.0.0.1:8000/docs` |
| OpenAPI JSON | `http://127.0.0.1:8000/api/v1/openapi.json` |
| Test de conexión DB | `http://127.0.0.1:8000/db-test-events` |

## Documentos fuente

Esta documentación consolida y actualiza los siguientes informes del proyecto:

- `funcionalidades_end_to_end.md`
- `informe_arquitectura_android.md`
- `Quickvnt_Arquitectura_MVP (2).md`

Los endpoints documentados reflejan la **implementación actual** del backend en este repositorio.

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
