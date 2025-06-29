# product-service/Dockerfile

# 1. Use JDK 17 base image
FROM eclipse-temurin:17-jdk

# 2. Create app directory
WORKDIR /app

# 3. Copy Spring Boot jar (ensure you've built it)
COPY target/product-service-*.jar app.jar

# 4. Expose the internal port used by the service
EXPOSE 8081

# 5. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
