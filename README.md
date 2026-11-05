# Chat Application

A real-time chat application with accounts, rooms, direct messages, and persistent message history. The backend uses Spring Boot and PostgreSQL; the frontend uses React and Vite.

## Requirements

- Java 21
- Node.js 20 or later and npm
- Docker Desktop (or Docker Engine) with the Docker Compose plugin
- Git

## Run locally

Run each service in its own terminal. From the repository root:

### 1. Configure the environment

Create the backend and database environment file:

```sh
cp .env.example .env
```

Edit `.env` and replace `POSTGRES_PASSWORD` and `JWT_SECRET` with private values. Use a random `JWT_SECRET` at least 32 bytes long. Keep `.env` local; it is ignored by Git.

Create the frontend environment file:

```sh
cp frontend/.env.example frontend/.env
```

The default frontend URLs point to the local backend and WebSocket endpoint. You can keep them unchanged for local development.

### 2. Start PostgreSQL with Docker Compose

Make sure Docker Desktop or Docker Engine is running, then run from the repository root:

```sh
docker compose up -d
```

Check that PostgreSQL is ready:

```sh
docker compose ps
docker compose logs postgres
```

The database is published on `127.0.0.1:5432` by default and uses the named `postgres_data` volume, so its data persists when the container stops. To stop it while preserving data, run:

```sh
docker compose down
```

### 3. Start the backend

In a new terminal, go to the backend directory and load the root `.env` variables into the current shell.

macOS or Linux with zsh/bash:

```sh
cd backend
set -a
source ../.env
set +a
./mvnw spring-boot:run
```

PowerShell:

```powershell
cd backend
$envLines = Get-Content ../.env | Where-Object { $_ -match '^[A-Za-z_][A-Za-z0-9_]*=' -and $_ -notmatch '\$\{' }
foreach ($line in $envLines) {
  $name, $value = $line -split '=', 2
  Set-Item -Path "Env:$name" -Value $value
}
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:$($env:POSTGRES_PORT)/$($env:POSTGRES_DB)"
./mvnw.cmd spring-boot:run
```

Spring Boot starts on `http://localhost:8080` and Flyway applies pending migrations during startup. Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

### 4. Start the frontend

In another terminal, from the repository root:

```sh
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vite, usually `http://localhost:5173`. Keep the terminal open while using the development server. Stop it with `Ctrl+C`.

### Troubleshooting

- If the backend cannot connect to PostgreSQL, check `docker compose ps`, confirm the database is healthy, and make sure `SPRING_DATASOURCE_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, and `POSTGRES_DB` in `.env` match.
- If port `5432` is already in use, set `POSTGRES_PORT` in `.env` to another free port. The datasource URL uses that variable when the shell expands `.env`.
- If the browser cannot reach the backend, check that `FRONTEND_ORIGIN` in `.env` matches the frontend URL and that `VITE_API_BASE_URL` in `frontend/.env` points to the backend.
- `docker compose down` preserves database data. `docker compose down -v` deletes the named volume and permanently removes the local database data.

The backend runs Flyway migrations at startup. Never edit a database schema manually; add a new migration under `backend/src/main/resources/db/migration`.

## Environment variables

See `.env.example` and `frontend/.env.example`. The backend requires a PostgreSQL URL and credentials, `JWT_SECRET`, and `FRONTEND_ORIGIN`.

## Tests and build

- Backend: start PostgreSQL and export the variables from `.env`, then run `./mvnw test` from `backend`.
- Frontend tests: run `npm run test:run` from `frontend`.
- Frontend production build: run `npm run build` from `frontend`.

## Project plan

See [chat-app-issue-plan.md](chat-app-issue-plan.md) for milestones and acceptance criteria.
