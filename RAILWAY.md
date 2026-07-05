# Deploy to Railway

Step-by-step guide for **printer_springBE** on [Railway](https://railway.com).

---

## 1. Push code to GitHub

Make sure your repo contains the `SpringBoot_MAP` folder (or deploy from that folder as root).

```bash
git add .
git commit -m "Prepare Railway deployment"
git push origin main
```

Do **not** commit `application-local-secrets.properties` or `.env`.

---

## 2. Create Railway project

1. Go to https://railway.com and sign in with GitHub.
2. Click **New Project** → **Deploy from GitHub repo**.
3. Select your repository.
4. If the app is inside `SpringBoot_MAP`, open the service → **Settings** → **Root Directory** → set:
   ```
   SpringBoot_MAP
   ```
   (Skip if the repo root is already `SpringBoot_MAP`.)

---

## 3. Add PostgreSQL database

1. In your Railway project, click **+ New** → **Database** → **PostgreSQL**.
2. Railway creates the DB and exposes `PGHOST`, `PGPORT`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`.

No manual JDBC URL needed — the `railway` profile maps these automatically.

---

## 4. Link database to your app

1. Click your **app service** (not the database).
2. Go to **Variables** → **New Variable** → **Add Reference**.
3. Link all PostgreSQL variables from the database service (or Railway may auto-inject them when services are in the same project).

---

## 5. Set environment variables

In the **app service** → **Variables**, add these **required** variables:

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod,postgres,railway` |
| `JWT_SECRET` | Random string, **min 32 characters** (app will not start without this) |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |
| `MAIL_USERNAME` | `golane360@gmail.com` |
| `MAIL_PASSWORD` | Your Gmail **App Password** (16 chars) |
| `MAIL_FROM` | `golane360@gmail.com` |
| `JPA_DDL_AUTO` | `update` |

**PostgreSQL (critical):** In Variables → **Add Reference** → select PostgreSQL → add **`DATABASE_URL`**. Without this, healthcheck fails.

`PORT` is set automatically by Railway — do not override it.

### Generate JWT_SECRET (PowerShell)

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

---

## 6. Deploy

Railway builds from the `Dockerfile` automatically (`railway.toml` is configured).

1. Go to **Deployments** — build should start.
2. Wait until status is **Active**.
3. Open **Settings** → **Networking** → **Generate Domain** (e.g. `printer-springbe-production.up.railway.app`).

---

## 7. Test

```http
GET https://YOUR-RAILWAY-DOMAIN.up.railway.app/api/v1/health
```

Expected:

```json
{
  "status": { "code": "000000", "description": "SUCCESS" },
  "data": { "Health": { "up": true } }
}
```

**Send OTP:**

```http
POST https://YOUR-RAILWAY-DOMAIN.up.railway.app/api/v1/auth/send-otp
Content-Type: application/json

{
  "email": "your@gmail.com"
}
```

Check your Gmail inbox (and Spam folder).

---

## 8. Use in your mobile / frontend app

Set API base URL to:

```
https://YOUR-RAILWAY-DOMAIN.up.railway.app
```

Example endpoints:

| Action | URL |
|--------|-----|
| Health | `GET /api/v1/health` |
| Send OTP | `POST /api/v1/auth/send-otp` |
| Login | `POST /api/v1/auth/login` |
| Products | `GET /api/v1/products/my` |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| **Healthcheck failure** | Set `JWT_SECRET` (32+ chars). Link PostgreSQL → add `DATABASE_URL` reference to app service |
| Build fails | Check **Deployments** logs; ensure Root Directory is `SpringBoot_MAP` |
| DB connection error | PostgreSQL plugin added + `DATABASE_URL` or `PGHOST` linked to app service |
| OTP not received | Verify `MAIL_*` vars; use Gmail App Password, not normal password |
| 502 / app crash | Check deploy logs for `JWT_SECRET` or `Railway PostgreSQL is not configured` |
| Health check fails | Wait 2–3 min; open **Deploy logs** and fix the first ERROR line |

### View logs

Railway dashboard → your app service → **Deployments** → latest deploy → **View logs**

Look for:

```
Mail mode: SMTP — OTP will be delivered to the recipient inbox via smtp.gmail.com
Started PrinterSpringBeApplication
```

---

## Notes

- **Product images** (`uploads/`) use ephemeral disk on Railway — files are lost on redeploy. For production, use S3/Cloudinary later.
- **Free tier** has usage limits; upgrade if the app sleeps or hits quotas.
- **Rotate** Gmail App Password if it was ever shared in chat.

---

## Quick checklist

- [ ] GitHub repo connected
- [ ] Root directory = `SpringBoot_MAP` (if needed)
- [ ] PostgreSQL database added
- [ ] `SPRING_PROFILES_ACTIVE=prod,postgres,railway`
- [ ] `JWT_SECRET` set (32+ chars)
- [ ] `MAIL_*` Gmail variables set
- [ ] Public domain generated
- [ ] `/api/v1/health` returns success
