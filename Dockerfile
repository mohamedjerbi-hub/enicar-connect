# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application, skipping tests for faster deployment
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/enicar-connect-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render assigns dynamic ports, but 8080 is standard for Spring Boot within the container)
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
