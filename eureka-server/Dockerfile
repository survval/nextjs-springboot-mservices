# ./eureka-server/Dockerfile

# Use OpenJDK 17 for Spring Boot apps
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy the built JAR from host into container
COPY target/eureka-server-*.jar app.jar

# Expose Eureka's default port
EXPOSE 8761

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
