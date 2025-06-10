🔐 Authentication

**Login**
```
POST /checklist-manager/auth/login
Content-Type: application/login-request-v1+json
```

**Request:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response:**

```json
{
"accessToken": "<JWT_TOKEN>",
"expiresIn": 3600
}
```

Use this token in the Authorization header for all secured endpoints:
```
Authorization: Bearer <JWT_TOKEN>
```