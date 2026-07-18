<div align="center">

# 🎟️ Quickvnt Backend

### MVP SaaS de Gestión de Eventos MICE

*Meetings · Incentives · Conferences · Exhibitions*

<br/>

[![Python](https://img.shields.io/badge/Python-3.10+-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.110+-009688?style=for-the-badge&logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com/)
[![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-3FCF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com/)
[![Kotlin](https://img.shields.io/badge/Cliente-Android%20Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)

[![API REST](https://img.shields.io/badge/API-REST%20%2F%20JSON-blue?style=flat-square)](./docs/04-api-reference.md)
[![JWT](https://img.shields.io/badge/Auth-JWT%20%2B%20Supabase-orange?style=flat-square)](./docs/03-funcionalidades-end-to-end.md)
[![QR](https://img.shields.io/badge/QR-HMAC--SHA256-red?style=flat-square)](./docs/03-funcionalidades-end-to-end.md)
[![Docs](https://img.shields.io/badge/Docs-6%20guías-purple?style=flat-square)](./docs/README.md)

<br/>

**API REST en Python/FastAPI** para la plataforma Quickvnt — marketplace, tickets, check-in QR y analíticas en un solo backend modular.

[📖 Documentación](./docs/README.md) · [🚀 Inicio rápido](#-inicio-rápido) · [🔌 API](#-api--endpoints-principales) · [📦 Repo](https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE)

</div>

---

## 📋 Tabla de contenidos

- [Qué incluye este repositorio](#-qué-incluye-este-repositorio)
- [Documentación](#-documentación)
- [Arquitectura](#️-arquitectura)
- [Funcionalidades del MVP](#-funcionalidades-del-mvp)
- [Roles del sistema](#-roles-del-sistema)
- [Stack tecnológico](#️-stack-tecnológico)
- [Inicio rápido](#-inicio-rápido)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [API — Endpoints principales](#-api--endpoints-principales)
- [Seguridad](#-seguridad)
- [Información del proyecto](#-información-del-proyecto)

---

## 📦 Qué incluye este repositorio

| Componente | Descripción |
|:---:|:---|
| 🌐 **API REST** | Monolito modular FastAPI con 6 módulos de negocio |
| 🔐 **Autenticación** | Bridge hacia Supabase Auth (registro, login, refresh JWT) |
| 🗄️ **Base de datos** | PostgreSQL en Supabase con RLS, índices y migraciones |
| 📱 **Códigos QR** | Generación y validación con firma HMAC-SHA256 |
| 📚 **Documentación** | 6 guías técnicas en [`docs/`](./docs/README.md) |

> Este repositorio contiene el **backend**. El cliente móvil es una app Android en **Kotlin + Jetpack Compose**.

---

## 📚 Documentación

La documentación completa vive en [`docs/`](./docs/README.md). Consolida los informes originales del equipo:

| 📄 Informe fuente | Estado |
|:---|:---:|
| `funcionalidades_end_to_end.md` | ✅ Integrado |
| `informe_arquitectura_android.md` | ✅ Integrado |
| `Quickvnt_Arquitectura_MVP (2).md` | ✅ Integrado |

| Guía | Qué encontrarás |
|:---|:---|
| 📌 [Índice](./docs/README.md) | Punto de entrada y visión general |
| 🏗️ [Arquitectura del sistema](./docs/01-arquitectura-sistema.md) | Stack, módulos, seguridad QR, roadmap |
| 📱 [Arquitectura Android](./docs/02-arquitectura-android.md) | Clean Architecture, MVVM, CameraX, ML Kit |
| 🔄 [Funcionalidades E2E](./docs/03-funcionalidades-end-to-end.md) | 11 flujos con diagramas de secuencia |
| 🔌 [Referencia de API](./docs/04-api-reference.md) | Endpoints, DTOs, roles y errores |
| 🗃️ [Modelo de datos](./docs/05-modelo-datos.md) | Tablas, JSONB, índices y RLS |
| ⚙️ [Instalación y despliegue](./docs/06-instalacion-despliegue.md) | Setup, `.env`, Docker y producción |

---

## 🏗️ Arquitectura

<p align="center">
  <img src="docs/assets/Arquitectura.png" alt="Arquitectura general del sistema Quickvnt" width="900" />
</p>

> 🔒 La app Android **nunca habla directo con Supabase** — toda la lógica de negocio pasa por esta API.

---

## ✨ Funcionalidades del MVP

| # | Funcionalidad | Módulos |
|:---:|:---|:---|
| 1️⃣ | Registro e inicio de sesión con JWT | `auth` · `users` |
| 2️⃣ | Marketplace de eventos con filtros y paginación | `events` |
| 3️⃣ | Creación de eventos con formulario dinámico (JSONB) | `events` |
| 4️⃣ | Registro a eventos con control de aforo transaccional | `tickets` |
| 5️⃣ | Emisión de tickets con QR firmado (HMAC-SHA256) | `tickets` |
| 6️⃣ | Validación de accesos en campo (check-in idempotente) | `checkin` |
| 7️⃣ | Dashboard analítico con KPIs en tiempo real (polling) | `analytics` |
| 8️⃣ | Gestión de staff por evento | `events` |

➡️ Detalle de cada flujo en [Funcionalidades E2E](./docs/03-funcionalidades-end-to-end.md)

---

## 👥 Roles del sistema

<table>
<tr>
<td align="center" width="33%">

### 👤 ATTENDEE

Explorar marketplace  
Registrarse a eventos  
Ver tickets y presentar QR

</td>
<td align="center" width="33%">

### 🎯 ORGANIZER

Crear y publicar eventos  
Definir aforo y formularios  
Asignar staff · Ver analíticas

</td>
<td align="center" width="33%">

### 🛡️ STAFF

Escanear QRs en puerta  
Validar accesos en eventos asignados

</td>
</tr>
</table>

---

## 🛠️ Stack tecnológico

<table>
<tr>
<th>🐍 Backend <em>(este repo)</em></th>
<th>📱 Cliente móvil <em>(repo separado)</em></th>
</tr>
<tr>
<td>

| Componente | Tecnología |
|:---|:---|
| Lenguaje | Python 3.10+ |
| Framework | FastAPI + Uvicorn |
| Base de datos | Supabase (PostgreSQL) |
| Auth | Supabase Auth (bridge) |
| ORM | SQLModel / SQLAlchemy async |
| Validación | Pydantic v2 |
| QR | `qrcode` + Pillow |
| JWT | `python-jose` / PyJWT |

</td>
<td>

`Kotlin` · `Jetpack Compose` · `Hilt`  
`Retrofit` · `Room` · `DataStore`  
`CameraX` · `ML Kit` · `Coil`

</td>
</tr>
</table>

---

## 🚀 Inicio rápido

```bash
# 1. Clonar
git clone https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE.git
cd Proyecto-final-ultimate-movil-BE

# 2. Entorno virtual
python3 -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\Activate.ps1

# 3. Dependencias y configuración
pip install -r requirements.txt
cp .env.example .env             # ← Completar credenciales de Supabase

# 4. Levantar servidor
uvicorn app.main:app --reload
```

### ☁️ Configurar Supabase

| Paso | Acción |
|:---:|:---|
| 1 | Crear proyecto en [supabase.com](https://supabase.com) |
| 2 | Ejecutar [`schema.sql`](./schema.sql) en el SQL Editor |
| 3 | Si la DB ya existía → [`migrations/001_add_staff_role.sql`](./migrations/001_add_staff_role.sql) |
| 4 | Copiar credenciales al `.env` → [guía completa](./docs/06-instalacion-despliegue.md) |

### ✅ Verificar que funciona

| Recurso | URL |
|:---|:---|
| 🏠 API raíz | http://127.0.0.1:8000/ |
| 📖 Swagger UI | http://127.0.0.1:8000/docs |
| 📄 OpenAPI JSON | http://127.0.0.1:8000/api/v1/openapi.json |
| 🔌 Test DB | http://127.0.0.1:8000/db-test-events |

---

## 📁 Estructura del proyecto

```
quickvnt-backend/
│
├── 📂 app/
│   ├── main.py              # FastAPI, routers, CORS
│   ├── core/                # Config, JWT, DB async
│   ├── auth/                # Registro, login, refresh
│   ├── users/               # Perfiles ATTENDEE / ORGANIZER / STAFF
│   ├── events/              # CRUD, marketplace, staff
│   ├── tickets/             # Registro, QR firmado
│   ├── checkin/             # Validación en puerta
│   └── analytics/           # KPIs organizador
│
├── 📂 docs/                  # Documentación técnica (6 guías)
├── 📂 migrations/            # Migraciones SQL
├── schema.sql               # Esquema inicial PostgreSQL
├── requirements.txt
└── .env.example
```

---

## 🔌 API — Endpoints principales

| Método | Endpoint | Auth | Descripción |
|:---:|:---|:---:|:---|
| `POST` | `/api/v1/auth/register` | — | Registro de usuario |
| `POST` | `/api/v1/auth/login` | — | Inicio de sesión |
| `POST` | `/api/v1/auth/refresh` | — | Renovar tokens JWT |
| `GET` | `/api/v1/events` | — | 🏪 Marketplace (público) |
| `POST` | `/api/v1/events` | ORGANIZER | Crear evento |
| `GET` | `/api/v1/events/mine` | ORGANIZER | Mis eventos |
| `POST` | `/api/v1/tickets/register/{event_id}` | ✓ | Registrarse a evento |
| `GET` | `/api/v1/tickets/me` | ✓ | Mis tickets |
| `POST` | `/api/v1/checkin/validate` | STAFF / ORG | Validar QR en puerta |
| `GET` | `/api/v1/analytics/events/{event_id}` | ORGANIZER | Dashboard |
| `POST` | `/api/v1/events/{id}/staff` | ORGANIZER | Asignar staff |

📎 Contrato completo → [Referencia de API](./docs/04-api-reference.md)

---

## 🔒 Seguridad

| Capa | Mecanismo |
|:---|:---|
| 🌐 Transporte | HTTPS/TLS en todas las comunicaciones |
| 🎫 Sesión | JWT de Supabase validado en cada endpoint protegido |
| 📱 QR | Firma HMAC-SHA256 — clave `QR_JWT_SECRET` solo en servidor |
| 🚫 Anti-reuso | Check-in idempotente — un ticket, un solo ingreso |
| 🛡️ Base de datos | Row-Level Security (RLS) en PostgreSQL |
| 👥 Aforo | `SELECT FOR UPDATE` — sin sobre-registro concurrente |

---

<div align="center">

## 👨‍💻 Información del proyecto

| | |
|:---|:---|
| **Repositorio** | [Proyecto-final-ultimate-movil-BE](https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE) |
| **Grupo** | **Quickvnt** |
| **Salón** | **1SF-241** |

| Integrante | Cédula |
|:---|:---|
| Fong, Enrique | 4-829-300 |
| González, Jabneel | 8-990-229 |
| Guillén, Manuel | 8-1016-1618 |
| Lu, Joaquín | 8-1024-2466 |
| Santimateo, Diego | 9-764-2382 |
| Pimentel, David | 8-1010-750 |

<br/>

**Quickvnt** — Gestión inteligente de eventos MICE 🎟️

*Backend API · Python/FastAPI · Supabase*

</div>
