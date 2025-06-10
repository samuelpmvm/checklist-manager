🔐 Configuration

- Configuration is managed through environment variables and Helm charts.
    - DB_USER, DB_PASSWORD, DB_NAME
    - JWT_SECRET_KEY

- Database schema is created using Liquibase
- Helm uses values.yaml to inject environment-specific configs