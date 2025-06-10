🧾 API Overview

All endpoints are prefixed with: /checklist-manager/api/v1

🧍 User
- POST /auth/login — Login

- POST /auth/register — Register user

- GET /auth/me — Get logged-in user info

✅ Checklists
- GET /checklist — List all checklists

- POST /checklist — Create a checklist

- GET /checklist/{id} — Get checklist by ID

- PUT /checklist/{id} — Update a checklist

- DELETE /checklist/{id} — Delete a checklist

📋 Checklist Items

- GET /checklist/{id}/items — List all items in a checklist

- POST /checklist/{id}/items — Add item to a checklist

- PUT /checklist/{id}/items/{itemId} — Update checklist item

```
http://localhost:8080/checklist-manager/swagger-ui/index.html
```