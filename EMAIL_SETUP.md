# Email setup (OTP)

## Gmail App Password not available?

Google shows *"The setting you are looking for is not available for your account"* when:

- The account is **work / school** (Google Workspace), not personal `@gmail.com`
- **Advanced Protection** is on
- 2-Step Verification uses **only security keys** (no SMS/authenticator backup)
- Google disabled App Passwords for your account type

### Try enabling App Passwords (personal Gmail only)

1. https://myaccount.google.com/security
2. **2-Step Verification** → add **Phone** or **Authenticator app** (not security key only)
3. Wait 24 hours, then try https://myaccount.google.com/apppasswords again

If it still does not appear, use **Brevo** below.

---

## Recommended: Brevo (free SMTP, ~5 minutes)

1. Sign up: https://www.brevo.com (free tier)
2. **SMTP & API** → **Generate a new SMTP key** → copy the key
3. **Senders & IP** → **Add a sender** → `golane360@gmail.com` → verify via email link Google sends you
4. Edit `src/main/resources/application-local-secrets.properties`:

```properties
app.mail.embedded=false
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=YOUR_BREVO_ACCOUNT_LOGIN_EMAIL
spring.mail.password=YOUR_BREVO_SMTP_KEY
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
app.mail.from=golane360@gmail.com
```

5. Restart the app → call `POST /api/v1/auth/send-otp` with `"email": "golane360@gmail.com"`

OTP will arrive in the **golane360@gmail.com** inbox (check Spam once).
