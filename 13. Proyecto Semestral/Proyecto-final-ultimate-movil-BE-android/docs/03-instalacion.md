# 03 — Instalación y configuración

> **Grupo:** Quickvnt · **Salón:** 1SF-241

## Requisitos del sistema

### Para desarrollo Android

| Requisito | Versión mínima |
|-----------|----------------|
| Android Studio | Ladybug (2024.2) o superior |
| JDK | 11+ |
| Gradle | Incluido (wrapper) |
| Android SDK | API 26 (min) — API 37 (target) |
| RAM recomendada | 8 GB+ |

### Para backend local (opcional)

| Requisito | Versión |
|-----------|---------|
| Python | 3.10+ |
| pip / uv | Última |
| PostgreSQL | 14+ (o SQLite para dev) |

---

## Opción A — Usar API en producción (recomendado)

La app ya está configurada para usar el backend desplegado en Render. No necesitas levantar el servidor localmente.

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE.git
cd Proyecto-final-ultimate-movil-BE

# 2. Navegar a la carpeta del frontend Android
# (la ruta depende de la rama — puede estar en raíz o en subcarpeta)

# 3. Abrir en Android Studio
# File → Open → seleccionar carpeta con build.gradle.kts

# 4. Esperar sincronización de Gradle

# 5. Ejecutar en emulador o dispositivo
# Run ▶️ o: ./gradlew :app:installDebug
```

**API configurada:** `https://pruebasemestral.onrender.com/api/v1/`

> **Nota:** Render puede tardar ~30s en "despertar" el servidor si estuvo inactivo.

---

## Opción B — Backend local

### 1. Levantar el backend

```bash
# Desde la raíz del repo (rama master)
cd Proyecto-final-ultimate-movil-BE

# Instalar dependencias
pip install -r requirements.txt

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tu DATABASE_URL y SECRET_KEY

# Ejecutar migraciones (si aplica)
# psql ... < schema.sql

# Iniciar servidor
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 2. Configurar la URL en Android

Edita `app/build.gradle.kts`:

```kotlin
// Emulador Android → localhost de tu PC
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/api/v1/\"")

// Dispositivo físico → IP de tu PC en la red local
buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.X:8000/api/v1/\"")
```

| Entorno | URL base |
|---------|----------|
| Emulador | `http://10.0.2.2:8000/api/v1/` |
| Dispositivo físico | `http://TU_IP:8000/api/v1/` |
| Producción | `https://pruebasemestral.onrender.com/api/v1/` |

### 3. Sincronizar y ejecutar

```bash
./gradlew :app:assembleDebug
```

---

## Google Maps (opcional)

Para ver el mapa en el detalle del evento:

1. Obtén una API Key en [Google Cloud Console](https://console.cloud.google.com/)
2. Habilita **Maps SDK for Android**
3. Crea el archivo `local.properties` en la raíz del proyecto:

```properties
sdk.dir=/ruta/a/tu/Android/sdk
MAPS_API_KEY=AIzaSy...
```

4. Sincroniza Gradle y recompila.

> Sin API Key, el resto de la app funciona; solo el mapa no se mostrará.

---

## Ejecutar tests

```bash
# Tests unitarios
./gradlew :app:testDebugUnitTest

# Tests instrumentados (requiere emulador/dispositivo)
./gradlew :app:connectedDebugAndroidTest
```

El proyecto incluye `ApiConnectivityTest.kt` que verifica conectividad con la API.

---

## Solución de problemas

| Problema | Solución |
|----------|----------|
| Gradle sync failed | File → Invalidate Caches → Restart |
| `Cleartext HTTP not permitted` | Usa HTTPS o configura `network_security_config.xml` |
| API timeout en Render | Espera 30s y reintenta (cold start) |
| Emulador no conecta a localhost | Usa `10.0.2.2` en lugar de `127.0.0.1` |
| Dispositivo no conecta | Verifica misma red WiFi y firewall |
| `MAPS_API_KEY` vacío | Agrega la clave en `local.properties` |

---

## Build de release

```bash
./gradlew :app:assembleRelease
```

El APK se genera en:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

<!-- TODO: Documentar proceso de firma si generan APK firmado -->

---

## Verificación de instalación exitosa

Comprueba que:

- La app abre sin crash
- La pantalla de splash / welcome es visible
- El login con usuario de prueba funciona
- El marketplace carga eventos desde la API
- La navegación entre tabs funciona según rol

**Usuarios de prueba:** <!-- TODO: Agregar credenciales de demo si existen -->

| Rol | Email | Contraseña |
|-----|-------|------------|
| Asistente | `attendee@demo.com` | `<!-- TODO -->` |
| Organizador | `organizer@demo.com` | `<!-- TODO -->` |
| Staff | `staff@demo.com` | `<!-- TODO -->` |

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
