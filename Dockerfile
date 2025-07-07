# Use official OpenJDK base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar from target
COPY target/slot-0.0.1-SNAPSHOT.jar app.jar

# Expose port (make sure it matches your server.port in application.properties)
EXPOSE 9095

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
