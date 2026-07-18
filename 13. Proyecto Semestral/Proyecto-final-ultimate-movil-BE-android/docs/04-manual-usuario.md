# 04 — Manual de usuario

> **Grupo:** Quickvnt · **Salón:** 1SF-241

Guía de uso de la aplicación Quickvnt, organizada por pantalla y rol.

---

## Inicio de la aplicación

### Pantalla de inicio

![Pantalla de inicio](assets/screenshots/pantalla_de_inicio.png)

Pantalla de bienvenida con animación y logo de Quickvnt. Redirige a login si eliges comenzar, o a la pantalla principal si hay sesión activa.

| Elemento | Acción |
|----------|--------|
| **Comenzar** | Ir a pantalla de inicio de sesión |

**Demo en video**

<video src="assets/videos/inicio.webm" controls width="480">
  <a href="assets/videos/inicio.webm">Ver demo — inicio y autenticación</a>
</video>

---

## Autenticación

### Login

![Inicio de sesión](assets/screenshots/inicio_de_sesion.png)

1. Ingresa tu **correo electrónico**
2. Ingresa tu **contraseña**
3. Toca **Iniciar sesión**
4. La app detecta tu rol y te lleva a la pantalla correspondiente

### Registro

![Registro](assets/screenshots/registro.png)

1. Completa nombre, email y contraseña
2. Selecciona tu rol (**Asistente** u **Organizador**)
3. Toca **Registrarse**
4. Se crea la cuenta y se inicia sesión automáticamente

---

## Rol: Asistente (ATTENDEE)

**Demo en video**

<video src="assets/videos/atendee.webm" controls width="480">
  <a href="assets/videos/atendee.webm">Ver demo — flujo asistente</a>
</video>

### Marketplace

![Marketplace](assets/screenshots/atendee/marketplace.png)

- Explora eventos publicados
- Busca por nombre, lugar o categoría del evento
- Toca un evento para ver el detalle

### Detalle de evento

![Detalle de evento](assets/screenshots/atendee/pantalla_de_evento.png)

| Información | Descripción |
|-------------|-------------|
| Título y descripción | Datos del evento |
| Fecha y hora | Cuándo se realiza |
| Ubicación | Dirección + mapa (Google Maps) |
| Capacidad | Cupos disponibles |
| Botón **Registrarse** | Inscribirse al evento |

### Registro a evento

1. Desde el detalle, toca **Registrarse**
2. Completa el formulario dinámico (si el evento lo requiere)
3. Confirma el registro
4. Se genera tu ticket automáticamente

### Mis tickets

![Mis tickets](assets/screenshots/atendee/boletos.png)

- Lista de todos tus tickets activos
- Toca un ticket para ver el detalle y el **código QR**

### Ticket con QR

![Ticket QR](assets/screenshots/atendee/boleto_pantalla.png)

- Muestra el código QR para presentar en la entrada
- Incluye datos del evento y estado del ticket
- El staff escaneará este código para validar tu asistencia

### Perfil

![Perfil](assets/screenshots/perfil.png)

- Ver y editar tu nombre
- Cerrar sesión

---

## Rol: Organizador (ORGANIZER)

**Demo en video**

<video src="assets/videos/organizer.webm" controls width="480">
  <a href="assets/videos/organizer.webm">Ver demo — flujo organizador</a>
</video>

### Mis eventos

![Panel del organizador](assets/screenshots/organizer/panel_de_organizador.png)

- Lista de eventos que has creado
- Acciones: **Editar**, **Eliminar**, **Analytics**, **Staff**, **QR Scanner**
- Botón **+** para crear nuevo evento

### Crear evento

![Crear evento](assets/screenshots/organizer/crear_evento.png)

| Campo | Descripción |
|-------|-------------|
| Título | Nombre del evento |
| Descripción | Detalle para asistentes |
| Fecha inicio / fin | Rango del evento |
| Ubicación | Lugar físico |
| Categoría | Tipo de evento MICE |
| Capacidad | Máximo de asistentes |
| Estado | DRAFT / PUBLISHED |

### Editar evento

Misma interfaz que crear, con datos precargados. Permite cambiar cualquier campo y guardar.

### Gestión de staff

![Gestión staff](assets/screenshots/organizer/gestion_de_staff.png)

1. Desde un evento, toca **Gestionar staff**
2. Agrega usuarios staff por email
3. Elimina asignaciones si es necesario

### Analytics

![Analytics](assets/screenshots/organizer/dashboard_de_evento.png)

Dashboard con KPIs del evento:

| Métrica | Descripción |
|---------|-------------|
| Total registros | Asistentes inscritos |
| Check-ins | Asistentes que ingresaron |
| Tasa de asistencia | Porcentaje check-in / registros |
| Ocupación | Registros vs capacidad |

### Escáner QR (organizador)

![Escáner QR](assets/screenshots/organizer/registroqr.png)

El organizador también puede escanear QR para validar asistencia. Ver sección Staff.

---

## Rol: Staff

### Pantalla de staff

![Pantalla de staff](assets/screenshots/Staff/pantalla_de_staff.png)

- Lista de eventos donde fuiste asignado como staff
- Selecciona un evento para abrir el escáner

### Escáner QR

1. Selecciona el evento
2. Apunta la cámara al código QR del asistente
3. La app valida el ticket contra la API
4. Muestra resultado: ✅ Válido / ❌ Inválido / ⚠️ Ya usado

> El check-in con QR también se muestra en la [demo del organizador](assets/videos/organizer.webm).

---

## Navegación general

La app usa **navegación adaptativa** (bottom bar) según el rol:

| Tab | ATTENDEE | ORGANIZER | STAFF |
|-----|----------|-----------|-------|
| Inicio | Descubrir | Mis Eventos | Staff Events |
| Tickets | Boletos | — | — |
| Perfil | Perfil | Perfil | Perfil |

---

## Estados de ticket

| Estado | Significado |
|--------|-------------|
| `ACTIVE` | Ticket válido, sin usar |
| `USED` | Check-in realizado |
| `CANCELLED` | Registro cancelado |

---

## Preguntas frecuentes

**¿Puedo usar la app sin internet?**  
No en el MVP. Se requiere conexión para autenticación y sincronización con la API.

**¿El QR funciona sin conexión?**  
El QR se genera localmente, pero la validación requiere conexión a la API.

**¿Puedo cambiar de rol?**  
El rol se asigna al registrarse. Contacta al administrador para cambios.

---

## Reporte de errores

Reporta incidencias al **Grupo Quickvnt (Salón 1SF-241)** a través del repositorio del proyecto o contactando a cualquier integrante del equipo.

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

Si encuentras un bug, repórtalo en: [Issues del repositorio](https://github.com/ElRulios/Proyecto-final-ultimate-movil-BE/issues)
