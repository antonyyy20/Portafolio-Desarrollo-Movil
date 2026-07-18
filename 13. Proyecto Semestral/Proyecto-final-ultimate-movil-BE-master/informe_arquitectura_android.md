# Informe de Arquitectura, Diseño y Desarrollo de la Aplicación Móvil Quickvnt (Android)

## 1. Introducción

El presente informe tiene como objetivo documentar de manera exhaustiva la arquitectura de software, las decisiones de diseño y los criterios de desarrollo aplicados en la construcción de la aplicación móvil cliente de **Quickvnt**. Esta aplicación, diseñada para la plataforma Android utilizando el lenguaje **Kotlin**, representa la interfaz principal a través de la cual los tres actores del sistema (asistentes, organizadores y personal de staff) interactúan con la plataforma SaaS de gestión de eventos MICE (*Meetings, Incentives, Conferences and Exhibitions*). El objetivo central de la aplicación móvil es ofrecer una experiencia fluida, rápida y segura, tanto en el descubrimiento y registro a eventos como en el proceso crítico de control de accesos mediante códigos QR en sitio.

Para cumplir con las exigencias de un producto de calidad comercial e industrial, el desarrollo del frontend móvil se estructuró bajo el paradigma de **Clean Architecture** (Arquitectura Limpia) y el patrón de diseño **MVVM** (Model-View-ViewModel). Estos enfoques permiten aislar completamente la interfaz de usuario de los detalles técnicos de red, bases de datos y persistencia local, lo que facilita el mantenimiento del código, incrementa la testabilidad del sistema y asegura que los flujos críticos (como el escaneo de tickets sin conexión o con baja conectividad) operen de manera eficiente y robusta.

---

## 2. Stack tecnológico del Frontend

La selección del conjunto de herramientas y librerías para el cliente móvil obedeció a las pautas y estándares modernos promovidos por Google para el desarrollo nativo en Android. Cada componente fue elegido estratégicamente para resolver necesidades concretas del negocio, reduciendo el código repetitivo (*boilerplate*) y maximizando la mantenibilidad del sistema.

### 2.1 Kotlin
Se adoptó **Kotlin** como lenguaje de programación único y oficial para el desarrollo del cliente móvil. Kotlin proporciona características avanzadas de seguridad en tipado, inferencia de tipos y prevención de referencias nulas en tiempo de compilación (*null safety*), lo que mitiga drásticamente las fallas comunes en producción. Su integración nativa con el compilador de Android y su soporte de primer nivel para la programación asíncrona mediante **corrutinas** fueron factores determinantes para su elección.

### 2.2 Jetpack Compose
Para la construcción de la interfaz gráfica se utilizó **Jetpack Compose**, el kit de herramientas declarativo moderno de Android. Compose elimina la necesidad de estructurar la UI mediante archivos XML y layouts complejos, permitiendo describir la interfaz de forma lógica mediante funciones de Kotlin que reaccionan automáticamente a los cambios de estado. Esto agiliza el desarrollo de componentes dinámicos y reutilizables, como formularios personalizados o elementos interactivos con animaciones integradas.

### 2.3 Hilt (Dagger)
La inyección de dependencias se gestionó a través de **Hilt**, un envoltorio especializado de Dagger para Android. Hilt automatiza la creación de gráficos de dependencias y gestiona de manera transparente el ciclo de vida de los objetos inyectados en componentes clave de Android, como ViewModels, Activities y Fragments. Esto permite la inyección modular de repositorios, clientes de red y bases de datos locales, facilitando el desacoplamiento de clases y posibilitando la realización de pruebas unitarias mediante dobles de prueba (*mocks*).

### 2.4 Retrofit y OkHttp
El acceso a la API REST del backend se implementó mediante **Retrofit**, configurado en combinación con **OkHttp** como motor HTTP subyacente. Esta dupla facilita la definición de interfaces de red tipadas en Kotlin y automatiza la serialización y deserialización de cargas útiles JSON. OkHttp aporta soporte para la configuración de interceptores y autenticadores personalizados, herramientas críticas para la inyección de credenciales JWT y la gestión automatizada del refresco de sesiones.

### 2.5 Room Database y Jetpack DataStore
La persistencia de información en el dispositivo móvil se divide en dos tecnologías complementarias:
*   **Room Database:** Se utiliza para la base de datos local relacional (SQLite), actuando como una capa de abstracción que proporciona consultas seguras en compilación y soporte reactivo mediante flujos. Es la encargada de almacenar en caché la información de eventos y tickets para permitir el acceso offline parcial.
*   **Preferences DataStore:** Reemplaza a la antigua API de `SharedPreferences`. Está construida sobre corrutinas y flujos, garantizando que el almacenamiento de datos simples clave-valor (como tokens de acceso y preferencias del usuario) se ejecute de forma asíncrona en hilos secundarios, evitando bloqueos en la interfaz de usuario.

### 2.6 CameraX y Google ML Kit Barcode Scanning
El escaneo físico de los boletos digitales en la entrada de los eventos se resolvió mediante **CameraX**, la API de Jetpack que simplifica el control físico de la cámara en múltiples dispositivos Android. Para el análisis inteligente de imágenes y la decodificación del código QR se integró la biblioteca de **ML Kit Barcode Scanning** de Google. Esta solución híbrida ejecuta la decodificación localmente en el dispositivo de manera eficiente, lo que optimiza la latencia de lectura durante el flujo de accesos.

### 2.7 Coil (Coroutines Image Loader)
Para la carga y renderizado asíncrono de banners de eventos alojados en el almacenamiento remoto de Supabase, se seleccionó **Coil**. Al estar construida sobre corrutinas de Kotlin, Coil realiza las peticiones de descarga de imágenes en hilos secundarios y gestiona de forma inteligente la memoria caché y el almacenamiento en disco local del teléfono, reduciendo el consumo de datos y mejorando el rendimiento visual al hacer scroll.

---

## 3. Arquitectura del diseño de software

### 3.1 Unidirectional Data Flow (UDF) y MVVM
La aplicación implementa el principio de **Flujo Unidireccional de Datos (UDF)** utilizando el patrón arquitectónico **MVVM**. En este esquema, el estado de la UI viaja únicamente en una dirección (del ViewModel hacia la vista de Compose) y los eventos de interacción del usuario viajan en la dirección opuesta (de la vista hacia el ViewModel).

La UI de Compose observa el estado expuesto por el ViewModel como un objeto inmutable de tipo `StateFlow`. Cuando el usuario interactúa con la pantalla (por ejemplo, al presionar "Registrarse"), la vista emite una acción o evento al ViewModel. El ViewModel recibe este evento, ejecuta la lógica asíncrona requerida a través de los casos de uso y emite un nuevo estado inmutable. Compose detecta esta actualización y redibuja de manera inteligente únicamente los componentes visuales modificados (proceso denominado recomposición).

### 3.2 Implementación de capas (Clean Architecture)
El código de la aplicación se divide en tres capas conceptuales estructuradas con dependencias estrictamente unidireccionales de afuera hacia adentro:

*   **Capa de Presentación:** Contiene los componentes visuales de Jetpack Compose organizados por pantallas de negocio y sus respectivos ViewModels. Esta capa depende únicamente de los Casos de Uso expuestos por la capa de dominio.
*   **Capa de Dominio:** Es el núcleo lógico libre de dependencias tecnológicas. Define las entidades puras del dominio (`Event`, `Ticket`, `Profile`) y los casos de uso (`GetAnalyticsUseCase`, `ValidateQrUseCase`, etc.). Asimismo, contiene las interfaces (contratos) de los repositorios que definen qué operaciones de datos son requeridas.
*   **Capa de Datos:** Implementa las interfaces de los repositorios de la capa de dominio. Es la encargada de coordinar las llamadas de red (Retrofit) y la persistencia local (Room / DataStore). Transforma los objetos de transferencia de datos de red (`DTOs`) y entidades de base de datos locales en modelos de dominio puros mediante funciones de mapeo (*mappers*), asegurando que ninguna otra capa conozca los detalles de serialización de la API o la base de datos.

---

## 4. Estructura del proyecto

La organización del código fuente del cliente móvil sigue una estrategia híbrida basada en **capas de arquitectura** para el núcleo del sistema, y **módulos funcionales** para la interfaz de usuario. Esta estructuración garantiza la mantenibilidad y escalabilidad del código. La disposición de los paquetes principales es la siguiente:

```
frontend/app/src/main/java/com/example/frontend/
│
├── di/                     # Módulos de Hilt para inyección de dependencias global
│
├── core/                   # Componentes transversales compartidos por la aplicación
│   ├── network/            # Cliente Retrofit, interceptores y autenticadores de OkHttp
│   ├── session/            # Manejo del estado de sesión y almacenamiento en DataStore
│   └── ui/                 # Temas, tipografías y componentes Compose comunes
│
├── domain/                 # Capa de Dominio (Núcleo lógico e interfaces)
│   ├── model/              # Modelos de negocio puros (Event, Ticket, UserProfile)
│   ├── repository/         # Interfaces de repositorios (Contratos)
│   └── usecase/            # Casos de uso específicos por flujo de negocio
│
├── data/                   # Capa de Datos (Implementaciones de persistencia y red)
│   ├── remote/             # Clientes de red y DTOs de serialización HTTP
│   ├── local/              # Base de datos Room (Entidades, DAOs) y DataStore
│   └── repository/         # Implementaciones concretas de interfaces de repositorio
│
├── navigation/             # Configuración del enrutamiento y grafo de Compose Navigation
│
└── ui/                     # Capa de Presentación organizada por pantallas de negocio
    ├── auth/               # Pantallas y ViewModels de Login y Registro
    ├── marketplace/        # Listado de eventos y filtros del asistente
    ├── event_detail/       # Detalle de evento y respuesta al formulario dinámico
    ├── my_tickets/         # Listado de tickets del usuario y despliegue de códigos QR
    ├── checkin/            # Pantalla de escáner QR de Staff (CameraX + ML Kit)
    └── organizer/          # Panel de administración, aforo y analíticas del organizador
```

---

## 5. Ciclo de Red, Sesión y Seguridad en el Cliente

La comunicación con el backend está estrictamente protegida por un sistema de seguridad de doble capa implementado a nivel de networking de red y almacenamiento local:

### 5.1 Gestión y Refresco Silencioso de Tokens (JWT)
Cuando el usuario inicia sesión de forma exitosa, el backend retorna dos tokens JWT: un `access_token` de vida corta y un `refresh_token` de vida larga. La aplicación almacena estos tokens en el almacenamiento asíncrono de **Preferences DataStore** utilizando encriptación a nivel de sistema de archivos provista por la biblioteca Jetpack Security.

Para el consumo diario de endpoints protegidos, se inyecta un interceptor de OkHttp (`AuthInterceptor`) en la cadena de peticiones de Retrofit. Este interceptor añade la cabecera `Authorization: Bearer <access_token>` a cada solicitud de salida.

Si el `access_token` ha expirado, el servidor backend rechaza la petición devolviendo un código `HTTP 401 Unauthorized`. En este punto, entra en juego el componente **`AuthAuthenticator`** de OkHttp configurado en el cliente de red. Este componente captura síncronamente el error 401, pausa el hilo de llamadas de red y realiza una petición síncrona a `/api/v1/auth/refresh` enviando el `refresh_token` guardado. Al obtener la respuesta con los nuevos tokens, los sobrescribe en el `DataStore`, actualiza la petición original fallida con el nuevo `access_token` y la vuelve a enviar al backend de manera transparente para el usuario. Si la renovación de sesión falla por completo, la sesión se cierra de manera forzada y se redirige a la pantalla de Login.

---

## 6. Módulo de Escaneo de Acceso (CameraX & ML Kit)

El proceso de verificación de accesos es el elemento más crítico del sistema en campo. Para el staff, la app provee una pantalla optimizada con una interfaz enfocada en el rendimiento continuo.

### 6.1 Integración de Cámara en Compose
Jetpack Compose dibuja la UI de forma declarativa, pero el hardware de cámara nativo de Android requiere una vista tradicional para el renderizado continuo. Esto se resolvió utilizando el componente `AndroidView` de Compose, el cual permite incrustar vistas de la API clásica como `PreviewView` de **CameraX** dentro del layout declarativo.

### 6.2 Procesamiento de Frames y Análisis Local
La API de CameraX está configurada con dos casos de uso activos de forma paralela:
1.  **Preview:** Transmite la imagen en vivo de la cámara a la pantalla del dispositivo para que el staff apunte correctamente al boleto.
2.  **ImageAnalysis:** Recibe la corriente de frames de video en segundo plano a través de un pool de hilos secundario (`Executor`). Cada frame capturado se convierte en un formato de imagen compatible (`InputImage`) y se envía al detector local de **Google ML Kit Barcode Scanning**.

Una vez que ML Kit detecta y decodifica con éxito el texto firmado criptográficamente codificado en el código QR, la aplicación realiza un *throttling* o bloqueo de entrada temporal (desactivando la cámara durante 2 a 3 segundos) y reproduce una vibración corta en el dispositivo. De inmediato, el fragmento visual envía la cadena al ViewModel, que invoca a `ValidateQrUseCase` para procesar la validación física en el servidor a través de `POST /api/v1/checkin/validate`. Según el resultado (exitoso o duplicado/inválido), el ViewModel emite un estado que actualiza el color de la UI en pantalla (verde o rojo) y reproduce una alerta sonora correspondiente.

---

## 7. Concurrencia, Optimización de Rendimiento y Memoria

### 7.1 Gestión de Hilos con Corrutinas y Flow
Para evitar el congelamiento de la interfaz de usuario (*Application Not Responding - ANR*), todas las operaciones costosas se despachan fuera del hilo de ejecución principal:
*   **Lecturas y escrituras de bases de datos** (Room y DataStore) y **peticiones de red** (Retrofit) se ejecutan explícitamente en el hilo de fondo **`Dispatchers.IO`**.
*   **El análisis visual de imágenes** (decodificación de QR en ML Kit) se delega a un procesador con hilos dedicados para evitar degradar el rendimiento del procesamiento de la interfaz de usuario.
*   **La manipulación de estados de pantalla** se realiza en **`Dispatchers.Main`** para sincronizarse de forma segura con el ciclo de recomposición de Compose.

El flujo de datos reactivo se instrumenta mediante `StateFlow` en el ViewModel, el cual retiene el último estado de la UI de forma caliente. En la vista Compose, este flujo se consume mediante `.collectAsStateWithLifecycle()`, lo que garantiza que la pantalla solo escuche actualizaciones de estado cuando el ciclo de vida del fragmento/actividad esté activo (por ejemplo, en estado `STARTED` o `RESUMED`), liberando recursos del sistema cuando la pantalla está en segundo plano o el dispositivo está bloqueado.

---

## 8. Conclusiones

La arquitectura de la aplicación Android de **Quickvnt** ha sido diseñada bajo estándares rigurosos de desacoplamiento y reactividad moderna. La combinación de **Clean Architecture** y **MVVM** garantiza que las reglas de negocio permanezcan inalteradas ante cambios en la base de datos o en la implementación de la red API. 

El uso de tecnologías de punta del ecosistema Android —como **Jetpack Compose** para la interfaz declarativa, **Hilt** para una inyección de dependencias robusta, **CameraX** y **ML Kit** para el procesamiento local e inteligente de códigos QR, e interceptores inteligentes en **Retrofit** para el ciclo de vida seguro de la sesión— resulta en un cliente móvil de alto rendimiento, escalable y seguro. Esta sólida base arquitectónica proporciona un MVP robusto y preparado para evolucionar hacia un producto de escala comercial con un esfuerzo mínimo de refactorización de código.

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
