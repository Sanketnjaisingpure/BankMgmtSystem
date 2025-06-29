# Step 1: Use base image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy jar file
COPY target/BankManagementSystem-0.0.1-SNAPSHOT.jar myapp.jar

# Step 4: Expose port
EXPOSE 8080

# Step 5: Command to run app
CMD ["java", "-jar", "myapp.jar"]

# Step 6: Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
