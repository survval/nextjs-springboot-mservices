# tenant-registry-svc/Dockerfile

# 1. Use lightweight Java 17 base image
FROM eclipse-temurin:17-jdk

# 2. Set working directory inside the container
WORKDIR /app

# 3. Copy the compiled Spring Boot JAR into the image
COPY target/tenant-registry-svc-*.jar app.jar

# 4. Expose the service port (default 8082)
EXPOSE 8082

# 5. Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
