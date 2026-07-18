# Instalación y Despliegue

Guía para configurar el backend Quickvnt en entorno local y desplegarlo en producción.

---

## Requisitos previos

| Requisito | Versión mínima |
|---|---|
| Python | 3.10+ |
| pip | Última estable |
| Cuenta Supabase | Proyecto creado con DB + Auth + Storage |
| Git | Cualquier versión reciente |

---

## Instalación local

### 1. Clonar el repositorio

```bash
git clone https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE.git
cd Proyecto-final-ultimate-movil-BE
```

### 2. Crear entorno virtual

**macOS / Linux:**

```bash
python3 -m venv .venv
source .venv/bin/activate
```

**Windows (PowerShell):**

```powershell
python -m venv .venv
.venv\Scripts\Activate.ps1
```

### 3. Instalar dependencias

```bash
pip install -r requirements.txt
```

### 4. Configurar variables de entorno

```bash
cp .env.example .env
```

Editar `.env` con las credenciales de tu proyecto Supabase:

```env
PROJECT_NAME="Quickvnt API"
API_V1_STR="/api/v1"

# Supabase — Settings → API
SUPABASE_URL="https://tu-proyecto.supabase.co"
SUPABASE_KEY="tu-anon-public-key"
SUPABASE_SERVICE_ROLE_KEY="tu-service-role-key"
SUPABASE_JWT_SECRET="tu-jwt-secret"

# PostgreSQL — pooler Supabase (puerto 6543, transaction mode)
DATABASE_URL="postgresql://postgres.tu-ref:tu-password@aws-0-us-east-1.pooler.supabase.com:6543/postgres"

# Firma de códigos QR (generar string aleatorio largo)
QR_JWT_SECRET="clave-aleatoria-larga-y-segura"
```

**Dónde obtener cada valor en Supabase:**

| Variable | Ubicación en Supabase |
|---|---|
| `SUPABASE_URL` | Settings → API → Project URL |
| `SUPABASE_KEY` | Settings → API → anon public key |
| `SUPABASE_SERVICE_ROLE_KEY` | Settings → API → service_role key |
| `SUPABASE_JWT_SECRET` | Settings → API → JWT Secret |
| `DATABASE_URL` | Settings → Database → Connection string (pooler, port 6543) |

> **Seguridad:** Nunca commitear el archivo `.env`. Está incluido en `.gitignore`.

### 5. Inicializar la base de datos

1. Abrir el **SQL Editor** en el panel de Supabase.
2. Copiar y ejecutar el contenido de [`schema.sql`](../schema.sql).
3. Si la base ya existía sin el rol STAFF, ejecutar también [`migrations/001_add_staff_role.sql`](../migrations/001_add_staff_role.sql).

### 6. Ejecutar el servidor

```bash
uvicorn app.main:app --reload
```

El servidor estará disponible en `http://127.0.0.1:8000`.

---

## Verificación

| Prueba | URL / Comando | Resultado esperado |
|---|---|---|
| API activa | `GET http://127.0.0.1:8000/` | `{"message": "Bienvenido a la API de Quickvnt"}` |
| Conexión DB | `GET http://127.0.0.1:8000/db-test-events` | `{"status": "ok", "database": "conectada", ...}` |
| Swagger UI | `http://127.0.0.1:8000/docs` | Documentación interactiva |
| OpenAPI | `http://127.0.0.1:8000/api/v1/openapi.json` | Esquema JSON |

### Prueba rápida de registro

```bash
curl -X POST http://127.0.0.1:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@ejemplo.com",
    "password": "password123",
    "name": "Usuario Test",
    "role": "ATTENDEE"
  }'
```

---

## Ejecutar tests

```bash
pytest
```

---

## Despliegue en producción

### Opciones de hosting recomendadas

| Plataforma | Ventaja |
|---|---|
| **Railway** | Deploy desde GitHub, variables de entorno simples |
| **Render** | Free tier para MVPs, auto-deploy |
| **Fly.io** | Baja latencia, contenedores |

Supabase ya gestiona DB, Auth y Storage — solo se despliega la API FastAPI.

### Comando de producción

```bash
gunicorn app.main:app -w 4 -k uvicorn.workers.UvicornWorker --bind 0.0.0.0:8000
```

### Variables de entorno en producción

Configurar las mismas variables del `.env` en el panel del proveedor de hosting. Adicionalmente:

- Usar `SUPABASE_SERVICE_ROLE_KEY` solo en el servidor (nunca en el cliente).
- Generar un `QR_JWT_SECRET` único y largo (mínimo 32 caracteres aleatorios).
- Configurar CORS restringido al dominio de la app móvil (en producción, reemplazar `allow_origins=["*"]` en `app/main.py`).

### Reverse proxy (recomendado)

```
Internet → Nginx (TLS termination) → Gunicorn/Uvicorn (FastAPI)
```

Nginx maneja certificados SSL (Let's Encrypt) y reenvía tráfico al proceso ASGI.

---

## Docker (opcional)

Ejemplo de `Dockerfile`:

```dockerfile
FROM python:3.12-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000
CMD ["gunicorn", "app.main:app", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "--bind", "0.0.0.0:8000"]
```

```bash
docker build -t quickvnt-api .
docker run -p 8000:8000 --env-file .env quickvnt-api
```

---

## CI/CD con GitHub Actions (sugerido)

```yaml
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: "3.12"
      - run: pip install -r requirements.txt
      - run: pytest
```

---

## Troubleshooting

| Problema | Solución |
|---|---|
| Error de conexión a DB | Verificar `DATABASE_URL`, usar pooler (puerto 6543) |
| 401 en endpoints protegidos | Verificar `SUPABASE_JWT_SECRET` coincide con Supabase |
| QR inválido en check-in | Verificar `QR_JWT_SECRET` es el mismo que al generar tickets |
| CORS bloqueado desde app móvil | Agregar origen de la app en `CORSMiddleware` |
| Tablas no existen | Ejecutar `schema.sql` en SQL Editor de Supabase |

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
