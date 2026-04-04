# Use lightweight Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Expose port (Render will override)
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]