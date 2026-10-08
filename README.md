# GlamGest Backend

API REST para la gestión de clientes, citas, servicios, empleados y ventas.

## Requisitos

- Java 21
- MySQL 8
- Maven Wrapper

## Configuración

Definir estas variables antes de ejecutar la aplicación:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/glamgest_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=glamgest_app
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<secret-con-al-menos-32-caracteres>
CORS_ALLOWED_ORIGINS=http://localhost:4200
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
PRIVACY_POLICY_VERSION=1.0
GOOGLE_RECAPTCHA_SECRET_KEY=<secret-key-de-recaptcha-v2>
GOOGLE_RECAPTCHA_VERIFY_URL=https://www.google.com/recaptcha/api/siteverify
```

También puede configurarse la URL de verificación con `GOOGLE_RECAPTCHA_VERIFY_URL`; por defecto es `https://www.google.com/recaptcha/api/siteverify`.

Crear la base de datos con `glamgest_db.sql`. Para una base existente, limpiar duplicados de email y aplicar las migraciones de `database/migration` en orden antes de activar `validate`.

## Autenticación de clientes

`POST /api/auth/register` crea un usuario con rol `CLIENT` y crea o enlaza su perfil en `clients` usando el email. Si un administrador ya creó el cliente, el registro lo vincula sin duplicar sus citas.

`POST /api/auth/login` devuelve el token Bearer, el rol, los identificadores disponibles y el estado de aceptación de la política. Los usuarios con una política pendiente reciben `privacyPolicyRequired: true`.

`POST /api/auth/unlock` valida nuevamente la contraseña del usuario autenticado para desbloquear una sesión bloqueada por inactividad. Requiere el token Bearer vigente y recibe `{ "password": "..." }`. Devuelve `200` cuando la contraseña es válida y `401` cuando no lo es; no genera un token nuevo.

Los endpoints públicos de login y registro requieren un token válido de Google reCAPTCHA v2. El frontend debe enviar el valor de `g-recaptcha-response` como `recaptchaToken` en el cuerpo de la petición. El backend valida el token directamente con Google antes de autenticar o crear la cuenta.

El login también incluye el campo honeypot `website`. Debe enviarse vacío; el frontend debe mostrarlo fuera de la vista y fuera del orden de tabulación para que los usuarios reales no lo completen. Si contiene texto, el backend rechaza el login sin ejecutar la validación reCAPTCHA.

Ejemplo de login:

```json
{
  "email": "usuario@example.com",
  "password": "Password123!",
  "recaptchaToken": "token-generado-por-recaptcha",
  "website": ""
}
```

Para pruebas locales, el dominio `localhost` debe estar registrado en la clave de sitio de reCAPTCHA. La clave de sitio se usa únicamente en el frontend; `GOOGLE_RECAPTCHA_SECRET_KEY` se mantiene únicamente en el backend.

`PUT /api/auth/policy` acepta la versión vigente de la política para el usuario autenticado. Su cuerpo debe ser `{ "accepted": true }`. Mientras la política esté pendiente, el backend bloquea los demás endpoints protegidos.

La aceptación guarda también la fecha y hora en `privacy_policy_accepted_at`. Las bases existentes deben aplicar la migración `V7__add_privacy_policy_accepted_at.sql`.

`GET /api/auth/policy` devuelve la versión, fecha de vigencia y contenido de la política para que el frontend la muestre antes de solicitar la aceptación.

Los clientes autenticados pueden usar:

- `GET /api/clients/me`
- `POST /api/appointments` sin enviar `clientId`
- `GET /api/appointments/me`
- `PUT` y `DELETE` de sus propias citas

La creación de usuarios internos permanece restringida a `ADMIN`. El rol de un registro público siempre es `CLIENT`.

## Ejecución

```text
./mvnw spring-boot:run
```

Las pruebas usan H2 y se ejecutan con:

```text
./mvnw test
```

## Despliegue con Docker

La imagen utiliza Java 21 y escucha en el puerto definido por `PORT`, con `8080` como valor local predeterminado. Construir y ejecutar la imagen requiere que Docker Desktop esté iniciado.

En el entorno Docker local existente, los contenedores comparten la red `code_default` y MySQL está disponible con el alias `mysql` en el puerto interno `3306`:

```text
docker build -t glamgest-backend .
docker run --rm --network code_default -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/glamgest_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="<password>" \
  -e JWT_SECRET="<secret-de-al-menos-32-caracteres>" \
  -e JWT_EXPIRATION="3600000" \
  -e CORS_ALLOWED_ORIGINS="https://<dominio-del-frontend>" \
  -e GOOGLE_RECAPTCHA_SECRET_KEY="<secret-key>" \
  glamgest-backend
```

Si el backend existente ya ocupa el puerto `8080`, usar `-p 8081:8080` para probar esta imagen sin detenerlo. Desde Windows/Workbench, MySQL continúa siendo accesible mediante `localhost:3307`; ese puerto no debe usarse entre contenedores de la misma red.

En producción, configurar las variables directamente en el proveedor de hosting y no incluir secretos en el repositorio. La base de datos debe existir y tener las migraciones aplicadas antes de usar `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`.

## Duración de servicios y citas

`services.duration_minutes` es la duración base y debe ser mayor que cero y múltiplo de 15. Las nuevas duraciones válidas son `15`, `30`, `45`, `60`, `75`, etc.

Las citas guardan su duración efectiva en `appointments.duration_minutes`. Si el campo no se envía al crear una cita, se copia la duración base del servicio. Una duración enviada para una cita no modifica el servicio.

Ejemplo de cita con duración personalizada:

```json
{
  "appointmentDatetime": "2026-09-25T10:00:00",
  "clientId": 1,
  "employeeId": 2,
  "serviceId": 3,
  "durationMinutes": 90,
  "notes": "Cabello largo"
}
```

La duración de cada cita también debe ser múltiplo de 15. El backend rechaza solapamientos del mismo empleado con HTTP `409`; las citas `CANCELLED` y `NO_SHOW` no bloquean horarios.

Para bases existentes, ejecutar `database/migration/V5__add_appointment_effective_duration.sql`. La migración reporta duraciones de servicios inválidas y no modifica silenciosamente datos históricos.

## Sesión única y backups

La aplicación mantiene una sola sesión activa por usuario. Cada nuevo login invalida el token anterior; la base existente debe aplicar `database/migration/V8__add_active_session_id.sql` antes de usar `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`.

Los administradores pueden crear, consultar, descargar, restaurar y eliminar backups desde Configuración. Los archivos se guardan en `BACKUP_DIRECTORY` y se generan como SQL comprimido (`.sql.gz`). En Docker se utiliza el volumen `backup_data`; la imagen backend incluye `mysqldump` y el cliente `mysql`.

La restauración crea automáticamente un backup previo e invalida todas las sesiones. Para bases existentes, aplicar las migraciones pendientes antes de habilitar la restauración.

## Reactivación de servicios

`DELETE /api/services/{id}` desactiva el servicio sin eliminarlo físicamente. Para reactivarlo o actualizarlo, usar `PUT /api/services/{id}`; la búsqueda incluye servicios inactivos.

Los administradores pueden consultar activos e inactivos con `GET /api/services/admin`. El listado normal de `GET /api/services` continúa devolviendo únicamente servicios activos.

Ejemplo de reactivación:

```json
{
  "active": true
}
```

## Reportes administrativos

Los siguientes endpoints requieren la autoridad `ADMIN` y aceptan los parámetros opcionales `from` y `to` con formato `yyyy-MM-dd`. Si no se envían, se utiliza el mes actual hasta la fecha actual.

- `GET /api/reports/executive/pdf` - resumen ejecutivo.
- `GET /api/reports/appointments/pdf` - reporte de citas.
- `GET /api/reports/sales/excel` - detalle de ventas.
- `GET /api/reports/services/excel` - servicios más vendidos.
- `GET /api/reports/employees/excel` - rendimiento de empleados.

Los endpoints devuelven el archivo directamente como descarga (`application/pdf` o XLSX) y excluyen ventas anuladas de los reportes comerciales.

Al crear un servicio con un nombre que ya pertenece a un servicio inactivo, el backend actualiza y reactiva el mismo registro conservando su identificador. Si el nombre pertenece a un servicio activo, devuelve `409 Conflict`.
