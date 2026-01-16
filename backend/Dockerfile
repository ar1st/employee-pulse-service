# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from build stage (Spring Boot creates an executable JAR)
COPY --from=build /app/target/employee-pulse-service-*.jar app.jar

# Expose the application port
EXPOSE 8001

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

