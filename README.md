# printer_springBE

Spring Boot 4 backend for user registration (OTP), login, and product management.

## Requirements

- Java 21
- Maven 3.9+
- PostgreSQL 14+ (database: `mobile_pdb`)

## Quick start

### 1. Clone

```bash
git clone <your-repo-url>
cd printer_springBE
```

### 2. Create PostgreSQL database

```sql
CREATE DATABASE mobile_pdb;
```

### 3. Local secrets (required)

Copy the example file and edit with your values:

```bash
cp src/main/resources/application-local-secrets.properties.example \
   src/main/resources/application-local-secrets.properties
```

This file is **gitignored** — never commit passwords.

For local dev without real email, use embedded mail in secrets:

```properties
app.mail.embedded=true
```

For Gmail SMTP, set `app.mail.embedded=false` and Gmail App Password.  
See [EMAIL_SETUP.md](EMAIL_SETUP.md) if App Passwords are unavailable (use Brevo).

### 4. Database credentials (required for postgres profile)

Edit `src/main/resources/application-local-secrets.properties`:

```properties
POSTGRES_LOCAL_PASSWORD=your-postgres-password-here
```

Or set environment variable `DB_PASSWORD`.

Defaults in `application-postgres.properties`:

| Variable | Default |
|----------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/mobile_pdb` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` / `POSTGRES_LOCAL_PASSWORD` | *(required — set in secrets or env)* |

### 5. Run

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

App: `http://localhost:8080`

Active profiles: `local,postgres` (see `application.properties`).

---

## API overview

Base URL: `http://localhost:8080`

All responses use:

```json
{
  "status": { "code": "000000", "description": "SUCCESS" },
  "data": { "SectionName": { } }
}
```

### Auth (no token required)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/send-otp` | Send OTP to email |
| POST | `/api/v1/auth/verify-otp` | Verify OTP → `registrationToken` |
| POST | `/api/v1/auth/sign-up` | Register (header: `X-Registration-Token`) |
| POST | `/api/v1/auth/login` | Login → `accessToken` |

### Products (Bearer token required)

Header: `Authorization: Bearer <accessToken>`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/products/add` | Add product (JSON) |
| POST | `/api/v1/products/add-with-image` | Add product + image (multipart) |
| GET | `/api/v1/products/my` | List logged-in user's products |

### Health

| Method | Path |
|--------|------|
| GET | `/api/v1/health` |

---

## Add product (JSON example)

**POST** `/api/v1/products/add`  
**Header:** `Authorization: Bearer <accessToken>`  
**Content-Type:** `application/json`

```json
{
  "productName": "Biryani Gravy",
  "amount": 250.00,
  "quantity": 1.5,
  "quantityType": "KG",
  "parcelAmount": 20.00,
  "foodShifts": ["MORNING", "AFTERNOON"]
}
```

- `quantityType`: `KG` or `QTY`
- `foodShifts`: 1–3 of `MORNING`, `AFTERNOON`, `NIGHT`
- `parcelAmount`: optional

---

## Database tables

| Table | Purpose |
|-------|---------|
| `users` | Registered users |
| `otp_sessions` | OTP flow (temporary) |
| `products` | Products (`user_id` = owner) |
| `product_food_shifts` | Food shifts per product |

---

## Project structure

```
src/main/java/com/example/printer_springbe/
├── auth/          # OTP, sign-up, login, JWT
├── product/       # Add product, food shifts
├── common/        # ApiResponse, config, exceptions
└── controller/    # Health, dev mail inbox
```

---

## Environment variables

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | e.g. `local,postgres` |
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | DB user |
| `DB_PASSWORD` | DB password |
| `JWT_SECRET` | Min 32 chars (production) |
| `GMAIL_APP_PASSWORD` | Optional, for SMTP |

---

## Dev tools

- **H2 console:** disabled when using postgres profile
- **Dev mail inbox:** `GET /api/v1/dev/mails` (embedded mail mode only)
- **Uploaded images:** `http://localhost:8080/uploads/products/<file>`

---

## Build & test

```bash
./mvnw clean package
./mvnw test
```

---

## Security notes

- Passwords stored as BCrypt hashes
- JWT for authenticated product APIs
- Do not commit `application-local-secrets.properties`
- Rotate credentials if they were ever shared in chat

---

## License

Private project — internal use.
