# GoReserva – Sistema de gestión de reservas para canchas deportivas

Proyecto final de **Gestión de Proyectos de Software** · Universidad de Caldas · 2026-2
Equipo: Mariana Villegas (Product Owner) · Sebastián Rendón (Scrum Master) · Yobani Arenas Castaño (Desarrollo)

## Estructura

```
goreserva/
├── backend/    API REST en Spring Boot (Java 17)
├── frontend/   Aplicación web en Angular 17
└── database/   Script SQL de MySQL (tablas, restricciones y datos iniciales)
```

## Versiones requeridas

| Herramienta | Versión |
|---|---|
| Java (JDK) | 17 o superior |
| Maven | 3.9 o superior (o el que trae IntelliJ) |
| MySQL | 8.0 o superior |
| Node.js | 20 LTS |
| Angular CLI | 17 (`npm install -g @angular/cli@17`) |

## Instalación

### 1. Base de datos
```bash
mysql -u root -p < database/goreserva_schema.sql
```
Crea la base `goreserva` con sus tablas, 4 tipos de cancha, 3 canchas, horarios (6:00 a 23:00) y tarifas.

### 2. Backend
Revisen el usuario y la clave de MySQL en `backend/src/main/resources/application.properties`
(o definan las variables `DB_USER` y `DB_PASSWORD`).
```bash
cd backend
mvn spring-boot:run
```
Prueba: abrir http://localhost:8080/api/ping → `{"estado":"ok","tiposDeCancha":4}`

Al arrancar por primera vez se crea el administrador inicial:
**admin@goreserva.com / Admin123*** (cámbienlo después).

Pruebas unitarias: `mvn test`

### 3. Frontend
```bash
cd frontend
npm install
ng serve
```
Abrir http://localhost:4200

## Endpoints disponibles

| Método | Ruta | Acceso | Historia |
|---|---|---|---|
| GET | /api/ping | Público | HU-05 |
| POST | /api/auth/registro | Público | HU-06 |
| POST | /api/auth/login | Público | HU-07 |
| POST | /api/admin/usuarios | Solo ADMIN | HU-08 |

Ejemplo de registro:
```bash
curl -X POST http://localhost:8080/api/auth/registro -H "Content-Type: application/json" \
  -d '{"nombre":"Laura Gómez","correo":"laura@correo.com","telefono":"3001234567","password":"secreta123"}'
```

## Flujo de trabajo en Git (riesgo R08)

- `main`: versión estable, solo recibe merges desde `develop` al cierre de cada sprint.
- `develop`: integración del sprint.
- Ramas de trabajo: `feature/HU-XX-descripcion` creadas desde `develop`.
- Todo cambio entra por **pull request** a `develop`, con la revisión de al menos otro integrante.
- El mensaje de cada commit empieza con la clave de Jira, por ejemplo `GR-28 formulario de registro`.

Configuración en GitHub: *Settings → Branches → Add branch protection rule* para `main` y `develop`,
marcando **Require a pull request before merging** y **Require approvals (1)**.
