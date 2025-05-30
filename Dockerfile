# JDK base image
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy and build the project
COPY . .

#RUN ./mvnw clean package -DskipTests

# Final image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar checklist-app.jar

# Expose the application port
EXPOSE 8080

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "checklist-app.jar"]