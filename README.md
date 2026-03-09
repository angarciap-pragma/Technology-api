# technology-api

Microservicio para registrar tecnologias objetivo de los bootcamps.

## Historia de Usuario 1

**Como** admin  
**Necesito** registrar las tecnologias que seran usadas proximamente por las capacidades  
**Para** saber a que tecnologias le apunta el bootcamp y agrupar mejor las capacidades.

## Reglas de negocio

1. Cada tecnologia tiene 3 campos: `id`, `name`, `description`.
2. El nombre de la tecnologia no se puede repetir (sin diferencia entre mayusculas/minusculas).
3. Todas las tecnologias deben tener descripcion.
4. El nombre tiene maximo 50 caracteres.
5. La descripcion tiene maximo 90 caracteres.

## Stack tecnico

- Java 21
- Spring Boot 3.4.3
- Spring WebFlux
- Spring Data R2DBC (MySQL)
- Flyway
- OpenAPI / Swagger
- JUnit 5 + Reactor Test

## Arquitectura

El proyecto sigue enfoque de puertos y adaptadores:

- `domain`: reglas de negocio y casos de uso.
- `infrastructure/input/rest`: controlador REST y manejo de errores.
- `infrastructure/output/mysql`: persistencia en MySQL.

## Endpoint HU1

### Crear tecnologia

- **Metodo:** `POST`
- **Ruta:** `/api/v1/technologies`
- **HTTP success:** `201 Created`

### Obtener tecnologia por id (soporte HU3 capability-api)

- **Metodo:** `GET`
- **Ruta:** `/api/v1/technologies/{id}`
- **HTTP success:** `200 OK`

#### Request

```json
{
  "name": "Java",
  "description": "Lenguaje para desarrollo backend"
}
```

#### Response (201)

```json
{
  "id": 1,
  "name": "Java",
  "description": "Lenguaje para desarrollo backend"
}
```

#### Errores

- `400 Bad Request`: payload invalido o regla de dominio incumplida.
- `409 Conflict`: nombre de tecnologia duplicado.

Ejemplo `409`:

```json
{
  "timestamp": "2026-03-03T14:20:11.328Z",
  "status": 409,
  "message": "Technology name already exists",
  "path": "/api/v1/technologies"
}
```

## Base de datos

Migracion inicial (`V1__create_table_technologies.sql`):

- Tabla `technologies`
- Columnas:
  - `id` (PK autoincremental)
  - `name` (`VARCHAR(50)`)
  - `normalized_name` (`VARCHAR(50)`, unico)
  - `description` (`VARCHAR(90)`)

## Variables de entorno

Configurables en `application.properties`:

- `DB_HOST` (default: `localhost`)
- `DB_PORT` (default: `3306`)
- `DB_NAME` (default: `technology_db`)
- `DB_USER` (default: `technology_user`)
- `DB_PASSWORD` (default: `technology_pass`)

## Ejecucion local

1. Levantar MySQL y crear la base de datos `technology_db`.
2. Ejecutar el proyecto:

```bash
./gradlew bootRun
```

En Windows:

```powershell
.\gradlew.bat bootRun
```

La API queda en `http://localhost:8080`.

## Documentacion API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Pruebas

Ejecutar tests:

```bash
./gradlew test
```

En Windows:

```powershell
.\gradlew.bat test
```
