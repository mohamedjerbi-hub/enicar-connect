# ─── Stage 1: Build ───────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install Node.js for the frontend build (frontend-maven-plugin handles local node, but having it available helps edge cases, though the plugin downloads it locally)
# Actually, frontend-maven-plugin downloads its own Node.js, so just standard Maven is fine.

WORKDIR /app

# Copy Maven wrapper and pom first (layer cache)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copy the entire source (Java + Angular)
COPY src ./src

# Build everything (Angular will be built and copied to classes/static by Maven)
RUN ./mvnw clean package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs && chown -R spring:spring /app
USER spring

# Copy the monolith JAR (contains both Backend API and Frontend Static files)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
