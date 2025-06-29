# keycloak/Dockerfile

# 1. Use Eclipse Temurin Java 17 base image
FROM eclipse-temurin:17-jdk

# 2. Set working directory
WORKDIR /app

# 3. Copy built Spring Boot application JAR
COPY target/keycloak-*.jar app.jar

# 4. Expose the internal service port (assume 8090)
EXPOSE 8090

# 5. Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
