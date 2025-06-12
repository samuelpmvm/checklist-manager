# Checklist Manager ✅
[![Last Commit](https://img.shields.io/github/last-commit/samuelpmvm/checklist-manager)](https://github.com/samuelpmvm/checklist-manager)

[Build Status](https://github.com/samuelpmvm/checklist-manager/actions)

[![License](https://img.shields.io/github/license/samuelpmvm/checklist-manager)](LICENSE)

A Spring Boot RESTful API to manage checklists —  supporting item-level operations, JWT-based authentication, PostgreSQL with Liquibase, observability with Prometheus, and Kubernetes deployment via Helm. and integrated into CI/CD with GitHub Actions.

---

## 🔧 Features

- ✅ Create, retrieve, update, and delete checklists
- 📌 Manage checklist items (mark as DONE, BLOCKED, etc.)
- 🔐 JWT authentication with login and role-based access
- 🐘 PostgreSQL + Liquibase schema management
- 🐳 Docker and Docker Compose support
- ☸️ Kubernetes + Helm Chart for deployment
- 📊 Metrics available via Spring Actuator for Prometheus
- 🧪 Integrated GitHub Actions CI/CD pipeline

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

## 📚 Documentation

- [Authentication Guide](docs/authentication.md)
- [API Reference](docs/api.md)
- [Unit and Integration Tests](docs/tests.md)
- [Docker Usage](docs/docker.md)
- [Deployment with Helm](docs/deployment.md)
- [Metrics and Observability](docs/metrics.md)
- [Configuration](docs/configuration.md)
- [GitHub Actions CI/CD](docs/github_actions.md)
- [Licence](docs/license.md)
