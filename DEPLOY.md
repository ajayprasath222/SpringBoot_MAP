# Deployment guide

## Option 1 — Docker Compose (recommended)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed
- Gmail App Password (or Brevo SMTP)

### Steps

1. **Create environment file**
   ```bash
   cp .env.example .env
   ```
   Edit `.env` with your real `DB_PASSWORD`, `JWT_SECRET`, and Gmail SMTP values.

2. **Build and start**
   ```bash
   docker compose up -d --build
   ```

3. **Verify**
   ```bash
   curl http://localhost:8080/api/v1/health
   ```

4. **View logs**
   ```bash
   docker compose logs -f app
   ```

5. **Stop**
   ```bash
   docker compose down
   ```

API base URL: `http://localhost:8080`

---

## Option 2 — JAR on a VPS (Ubuntu)

### Prerequisites
- Java 21, PostgreSQL 16, Maven

### Steps

1. **Create database on server**
   ```sql
   CREATE DATABASE mobile_pdb;
   ```

2. **Build JAR**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Run with environment variables**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod,postgres
   export DB_URL=jdbc:postgresql://localhost:5432/mobile_pdb
   export DB_USERNAME=postgres
   export DB_PASSWORD=your-db-password
   export JWT_SECRET=your-jwt-secret-min-32-chars
   export MAIL_HOST=smtp.gmail.com
   export MAIL_PORT=587
   export MAIL_USERNAME=your@gmail.com
   export MAIL_PASSWORD=your-gmail-app-password
   export MAIL_FROM=your@gmail.com

   java -jar target/printer_springBE-0.0.1-SNAPSHOT.jar
   ```

4. **Run as a service** (systemd) or use `screen` / `tmux` for testing.

---

## Option 3 — Cloud (Railway / Render)

1. Push repo to GitHub.
2. Create a **PostgreSQL** database in the cloud dashboard.
3. Create a **Web Service** from the repo.
4. Set **Dockerfile** deploy (or Java build pack).
5. Add environment variables:

| Variable | Example |
|----------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod,postgres` |
| `DB_URL` | `jdbc:postgresql://host:5432/mobile_pdb` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | *(from cloud DB)* |
| `JWT_SECRET` | *(random 32+ chars)* |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |
| `MAIL_USERNAME` | `your@gmail.com` |
| `MAIL_PASSWORD` | *(Gmail app password)* |
| `MAIL_FROM` | `your@gmail.com` |
| `PORT` | `8080` *(set by platform)* |

6. Deploy and open the public URL + `/api/v1/health`.

---

## Production checklist

- [ ] `JWT_SECRET` is unique and at least 32 characters
- [ ] `DB_PASSWORD` is strong
- [ ] Gmail App Password (not normal password) for `MAIL_PASSWORD`
- [ ] Never commit `.env` or `application-local-secrets.properties`
- [ ] Rotate credentials if they were ever shared in chat
- [ ] Use HTTPS in front of the API (nginx, Caddy, or cloud load balancer)
- [ ] Product uploads: mount a persistent volume (`uploads/`)

---

## Environment variables reference

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Yes | `prod,postgres` |
| `DB_URL` | Yes | PostgreSQL JDBC URL |
| `DB_USERNAME` | Yes | DB user |
| `DB_PASSWORD` | Yes | DB password |
| `JWT_SECRET` | Yes | JWT signing key (min 32 chars) |
| `MAIL_HOST` | Yes | e.g. `smtp.gmail.com` |
| `MAIL_PORT` | No | Default `587` |
| `MAIL_USERNAME` | Yes | SMTP login |
| `MAIL_PASSWORD` | Yes | Gmail app password |
| `MAIL_FROM` | Yes | Sender email |
| `PORT` | No | Server port (default `8080`) |
| `JPA_DDL_AUTO` | No | Default `update` |
