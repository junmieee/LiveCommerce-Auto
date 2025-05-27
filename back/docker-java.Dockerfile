# Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY ./java /app
RUN gradle build --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]