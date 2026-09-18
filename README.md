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
```

Crear la base de datos con `glamgest_db.sql`. Para una base existente, limpiar duplicados de email y aplicar `database/migration/V2__link_clients_to_users.sql` antes de activar `validate`.

## Autenticación de clientes

`POST /api/auth/register` crea un usuario con rol `CLIENT` y crea o enlaza su perfil en `clients` usando el email. Si un administrador ya creó el cliente, el registro lo vincula sin duplicar sus citas.

`POST /api/auth/login` devuelve el token Bearer, el rol, el identificador del usuario y el identificador del cliente cuando existe.

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
