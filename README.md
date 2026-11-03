# Prezentownik

Aplikacja webowa do zarządzania listami prezentów i pomysłami prezentowymi.

Projekt tworzony jako praktyczna aplikacja dla rodziny i znajomych oraz projekt edukacyjny i portfolio.

## Technologie

**Backend**
- Java 25 LTS
- Spring Boot 4.1.2
- Spring MVC
- Maven
- JDBC
- Flyway

**Frontend**
- React
- TypeScript
- Vite
- ESLint

**Baza danych i infrastruktura**
- PostgreSQL 16
- Docker Compose
- GitHub Actions

## Architektura

Projekt wykorzystuje monorepo zawierające dwie oddzielne aplikacje:

- `backend/` — REST API oparte na Spring Boot
- `frontend/` — aplikacja React komunikująca się z API
- `compose.yaml` — konfiguracja lokalnej bazy PostgreSQL
- `.github/workflows/` — automatyczny build i testy

Frontend komunikuje się z backendem przez HTTP/JSON. Backend odpowiada za logikę biznesową i kontrolę dostępu do danych.

## Wymagania lokalne

- JDK 25
- Node.js 24 i npm
- Docker Desktop z Docker Compose
- Git

Poniższa instrukcja zakłada Windows i terminal Git Bash.

## Uruchomienie lokalne

### 1. Pobranie projektu

```bash
git clone https://github.com/adrian-wysocki/Prezentownik.git
cd Prezentownik
```

### 2. Konfiguracja środowiska

Skopiuj `.env.example` do `.env`:

```bash
cp .env.example .env
```

Ustaw własne hasło developerskie w `.env`. Plik ten nie powinien być commitowany.

### 3. PostgreSQL

Uruchom Docker Desktop, a następnie z katalogu głównego projektu:

```bash
docker compose up -d db
docker compose ps
```

Baza PostgreSQL jest dostępna lokalnie na porcie `5434`. Dane są przechowywane w wolumenie Dockera.

### 4. Backend

W osobnym terminalu Git Bash:

```bash
cd backend
set -a
source ../.env
set +a
./mvnw.cmd spring-boot:run
```

Backend jest dostępny pod adresem `http://localhost:8080`.

Endpoint techniczny:

`GET http://localhost:8080/api/health`

Oczekiwana odpowiedź:

```json
{"status":"UP"}
```

Migracje Flyway są wykonywane automatycznie podczas uruchamiania aplikacji.

### 5. Frontend

W kolejnym terminalu:

```bash
cd frontend
npm ci
npm run dev
```

Aplikacja jest dostępna pod adresem `http://localhost:5173`.

W środowisku developerskim Vite przekazuje żądania `/api` do Spring Boota na porcie `8080`.

## Testowanie i budowanie

**Backend**

W katalogu `backend/`:

```bash
./mvnw.cmd clean verify
```

**Frontend**

W katalogu `frontend/`:

```bash
npm run lint
npm run build
```

Build produkcyjny frontendu powstaje w katalogu `frontend/dist/`.

## Continuous Integration

GitHub Actions automatycznie uruchamia dwa workflowy po pushu do `main` i przy pull requestach kierowanych do `main`:

- Backend CI — Java 25, Maven, kompilacja i testy
- Frontend CI — Node.js 24, npm, ESLint, TypeScript i Vite

## Status projektu

**Milestone 0 — Fundament techniczny**

Podstawowa konfiguracja aplikacji, bazy danych, testów i CI.

Następny etap obejmuje implementację pierwszych funkcjonalności biznesowych.

## Planowany deployment

Docelowo aplikacja będzie uruchamiana w kontenerach Docker: frontend, backend i PostgreSQL. Konfiguracja produkcyjna zostanie przygotowana w późniejszym etapie projektu.
