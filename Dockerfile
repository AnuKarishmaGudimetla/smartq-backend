# Stage 1: Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Use OpenJDK to run the JAR
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/slot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
