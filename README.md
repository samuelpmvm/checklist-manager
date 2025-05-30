# Checklist Manager ✅

A RESTful API to manage checklists — built with Spring Boot, secured with JWT, containerized with Docker, deployed via Helm, and integrated into CI/CD with GitHub Actions.

---

## 🔧 Features

- RESTful CRUD API for managing checklists
- JWT-based authentication
- Prometheus metrics exposed via Spring Boot Actuator
- PostgreSQL database with Liquibase migration
- Docker and Docker Compose support
- Helm chart for Kubernetes deployment
- CI/CD with GitHub Actions
- ConfigMaps and Secrets for configuration management

---

## 🚀 Running Locally

### Requirements

- Docker
- Docker Compose

### Start the app

```bash
docker compose up -d --build

# Access the app at http://localhost:8080/checklist-manager/
```

🔐 Authentication
All endpoints are protected and require a JWT token.

🔑 Login
POST /checklist-manager/auth/login